package com.ecommerce.controller;

import com.ecommerce.entity.*;
import com.ecommerce.entity.enums.Category;
import com.ecommerce.repository.CartItemRepo;
import com.ecommerce.repository.OrderItemRepo;
import com.ecommerce.repository.ProductRepo;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.impl.CartServiceImpl;
import com.ecommerce.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;


@Controller
public class UserController {

    public static String uploadDir = System.getProperty("user.dir")+"/src/main/resources/static/productImages";

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartServiceImpl cartService;

    private Map<Integer, String> map = new HashMap<>();

    @PostConstruct
    public void init(){
        map.put(1, Category.BASKETBALL.name());
        map.put(2, Category.RUNNING.name());
        map.put(3, Category.HIKING.name());
        map.put(4, Category.CLIMBING.name());
        map.put(5, Category.SOCCER.name());
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home(Principal principal) {
        String username = principal.getName();
        UserDetails details = userService.loadUserByUsername(username);
        if (details != null && details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return "redirect:/admin/home/0";
        }
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model m, Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByUsername(username).orElseThrow();
        if (user.getCart() == null) {
            user.setCart(new Cart());
        }

        m.addAttribute("cartCount", user.getCart().getItems().size());

        m.addAttribute("categories", map);
        m.addAttribute("products", productRepo.findAll());

        return "shop";
    }

    @GetMapping("/shop")
    public String home(Model m) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, Category.BASKETBALL.name());
        map.put(2, Category.RUNNING.name());
        map.put(3, Category.HIKING.name());
        map.put(4, Category.CLIMBING.name());
        map.put(5, Category.SOCCER.name());

        m.addAttribute("categories", map);
        m.addAttribute("products", productRepo.findAll());

        return "shop";
    }

    @GetMapping("/shop/category/{id}")
    public String viewByCategory(Model m, @PathVariable("id") Integer id, Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByUsername(username).orElseThrow();

        if (user.getCart() == null) {
            user.setCart(new Cart());
        }

        m.addAttribute("cartCount", user.getCart().getItems().size());

        m.addAttribute("categories", map);
        String category = map.get(id);
        m.addAttribute("products", productRepo.findByCategory(category));

        return "shop";
    }

    @GetMapping("/shop/normal/category/{id}")
    public String viewByCategoryNormal(Model m, @PathVariable("id") Integer id) {

        m.addAttribute("categories", map);
        String category = map.get(id);
        m.addAttribute("products", productRepo.findByCategory(category));

        return "shop";
    }

    @GetMapping("/shop/viewproduct/{id}")
    public String viewProduct(Model m, @PathVariable("id") Integer id, Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByUsername(username).orElseThrow();
        if (user.getCart() == null) {
            user.setCart(new Cart());
        }

        m.addAttribute("cartCount", user.getCart().getItems().size());
        Product product = productRepo.findById(id).orElseThrow();
        m.addAttribute("product", product);
        Cart cart = user.getCart();
        m.addAttribute("inCart", cart.getItems().stream().filter(i -> i.getProduct().getId().equals(id)).findFirst().isEmpty());
        return "viewProduct";
    }

    @GetMapping("/shop/normal/viewproduct/{id}")
    public String viewProduct(Model m, @PathVariable("id") Integer id) {
        Product product = productRepo.findById(id).orElseThrow();
        m.addAttribute("product", product);
        return "viewProduct";
    }

    @GetMapping("/cart")
    public String viewCart(Model m, Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByUsername(username).orElseThrow();

        if (user.getCart() == null) {
            user.setCart(new Cart());
        }

        m.addAttribute("cartCount", user.getCart().getItems().size());
        m.addAttribute("cart", user.getCart().getItems());
        m.addAttribute("total", user.getCart().getItems().stream().mapToDouble(i -> i.getProduct().getPrice()).sum());
        return "cart";
    }

    @GetMapping("/cart/removeItem/{id}")
    public String removeCartItem(Model m, Principal principal, @PathVariable("id") Integer id) {
        cartItemRepo.deleteById(id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout/{total}")
    public String checkout(Model m, @PathVariable("total") Double total, Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByUsername(username).orElseThrow();

        m.addAttribute("cartCount", user.getCart().getItems().size());
        m.addAttribute("total", total);
        return "checkout";
    }

    @GetMapping("/paynow")
    public String pay(Model m, Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByUsername(username).orElseThrow();
        Cart cart = user.getCart();
        orderService.createOrder(cart.getId(), username);
        Map<String, Double> map = cart.getItems().stream().collect(Collectors.toMap(i ->i.getProduct().getName(), i -> i.getProduct().getPrice()));
        m.addAttribute("parameters", map);
        m.addAttribute("total", cart.getItems().stream().mapToDouble(i -> i.getProduct().getPrice()).sum());
        List<Integer> pIds = cart.getItems().stream().map(cartItem -> cartItem.getProduct().getId()).collect(Collectors.toList());
        cart.getItems().stream().forEach(cartItem -> cartItemRepo.deleteByProductId(cartItem.getProduct().getId()));
        for (Integer id: pIds){
            Product product = productRepo.findById(id).get();
            Integer oldQuantity = product.getQuantity();
            if(oldQuantity==1){
                product.setQuantity(0);
            }
            else {
                product.setQuantity(oldQuantity-1);
            }
            productRepo.save(product);
        }
        m.addAttribute("id", 43873423);
        return "orderPlaced";
    }

    @GetMapping("/addToCart/{id}")
    public String addToCart(Model m, @PathVariable("id") Integer id, Principal principal) {
        Product product = productRepo.findById(id).orElseThrow();
        String username = principal.getName();
        cartService.addItemToCart(product, username);
        return "redirect:/home";
    }

    @GetMapping("/admin/home/{id}")
    public String adminHome(Model m, @PathVariable("id") Integer id) {

        m.addAttribute("categories", map);
        if(id==0)
            m.addAttribute("products", productRepo.findAll());
        else{
            String category = map.get(id);
            m.addAttribute("products", productRepo.findByCategory(category));
        }

        return "products";
    }

    @GetMapping("/admin/products/add")
    public String productsAddGet(Model m) {
        m.addAttribute("categories", map);
        m.addAttribute("productDTO", new Product());
        return "productsAdd";
    }

    @PostMapping("/admin/products/add")
    public String productsAddPost(@ModelAttribute("productDTO") Product productDto,
                                  @RequestParam("productImage")MultipartFile file,
                                  @RequestParam("imgName")String imgName) throws IOException {

        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setCategory(productDto.getCategory());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());

        String imageUUID;
        if(!file.isEmpty()){
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.write(fileNameAndPath, file.getBytes());
        }
        else {
            imageUUID = imgName;
        }
        product.setImageName(imageUUID);
        productRepo.save(product);
        return "redirect:/admin/home/0";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String productDelete(@PathVariable("id") Integer id){
        cartItemRepo.deleteByProductId(id);
        orderItemRepo.deleteByProductId(id);
        productRepo.deleteById(id);
        return "redirect:/admin/home/0";
    }

    @GetMapping("/orders")
    public String orders(Model m){
        m.addAttribute("orders", orderService.getOrders());
        return "orders";
    }

    @GetMapping("/vieworder/{id}")
    public String viewOrderDetails(Model m, @PathVariable("id") Integer id) {
        User user = orderService.getOrder(id).get().getUser();
        Map<String, Integer> map = orderService.getOrder(id).get().getItems().stream().collect(Collectors.toMap(i ->i.getProduct().getName(), i -> i.getProduct().getQuantity()));
        m.addAttribute("parameters", map);
        m.addAttribute("id", id);
        m.addAttribute("user", user);
        m.addAttribute("total", orderService.getOrder(id).get().getItems().stream().mapToDouble(i -> i.getProduct().getPrice()).sum());
        return "viewOrder";
    }

    @GetMapping("/markorder/{id}")
    public String markOrder(Model m, @PathVariable("id") Integer id) {
        Order order = orderService.markStatus(id);
        return "redirect:/orders";
    }

    @GetMapping("/admin/product/update/{id}")
    public String productsUpdateGet(Model m, @PathVariable("id") Integer id) {
        m.addAttribute("categories", map);
        Product productDTO = productRepo.findById(id).orElseThrow();
        m.addAttribute("productDTO", productDTO);

        return "productsAdd";
    }

    @PostMapping("/admin/product/update/{id}")
    public String productsUpdatePost(Model m) {
        return "redirect:/admin/home/0";
    }
}
