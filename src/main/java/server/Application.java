package server;

import com.google.gson.Gson;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import server.model.Account;
import server.model.Wishlist;
import server.persistence.AccountRepository;
import server.persistence.ItemRepository;
import server.persistence.WishlistRepository;
import server.resources.Mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/user/*");

        return registrationBean;
    }

    @Bean
    @Autowired
    public AuthorizationModule initAuthorization(AccountRepository accountRepository,
                                                 WishlistRepository wishlistRepository,
                                                 ItemRepository itemRepository){
        AuthorizationModule.setAccountRepository(accountRepository);
        AuthorizationModule.setWishlistRepository(wishlistRepository);
        AuthorizationModule.setItemRepository(itemRepository);
        return new AuthorizationModule();
    }

    @Bean
    public VkApiClient vkApiClient() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        return new VkApiClient(transportClient, new Gson(), 3);
    }

    @Bean
    public Mapper classMapper() {
        return new Mapper();
    }

    @Bean
    @Profile("test")
    CommandLineRunner initTest(AccountRepository accountRepository,
                           WishlistRepository wishlistRepository,
                           ItemRepository itemRepository) {
        List<Account> list = new ArrayList<>();
        Arrays.asList("Anton,Kamill,Lola".split(",")).forEach(s -> {
            Account account = accountRepository.save(new Account(s));
            list.add(account);
            account.setRegistered(true);
        });
        list.get(0).getFriends().add(list.get(1));
        list.get(1).getFriends().add(list.get(0));
        list.get(0).setVkId(109317266);
        list.get(0).setPhotoLink("link1");
        accountRepository.save(list);
        Account first = accountRepository.getOne(2);

        Wishlist publicWishlist = new Wishlist("Public", list.get(0));
        publicWishlist.setVisibility(Wishlist.Visibility.PUBLIC);
        publicWishlist.setWishlistOrder(0);

        Wishlist privateWishlist = new Wishlist("Private", list.get(0));
        privateWishlist.setVisibility(Wishlist.Visibility.PRIVATE);
        privateWishlist.setWishlistOrder(1);

        Wishlist publicWithExcl = new Wishlist("Public with excl", list.get(0));
        publicWithExcl.setVisibility(Wishlist.Visibility.PUBLIC);
        publicWithExcl.setWishlistOrder(2);

        Wishlist privateWithExcl = new Wishlist("Private with excl", list.get(0));
        privateWithExcl.setVisibility(Wishlist.Visibility.PRIVATE);
        privateWithExcl.setWishlistOrder(3);

        List<Account> excl = new ArrayList<>();
        excl.add(first);
        wishlistRepository.save(publicWishlist);
        wishlistRepository.save(privateWishlist);
        wishlistRepository.save(publicWithExcl);
        wishlistRepository.save(privateWithExcl);
        publicWithExcl.setExclusions(excl);
        privateWithExcl.setExclusions(excl);
        wishlistRepository.save(publicWithExcl);
        wishlistRepository.save(privateWithExcl);
        return (evt) -> list.toArray();
    }

    @Bean
    @Profile("dev")
    CommandLineRunner initDev(AccountRepository accountRepository,
                           WishlistRepository wishlistRepository,
                           ItemRepository itemRepository) {
        Account account = new Account("Anton Prokopev");
        account.setVkId(109317266);
        return (evt) -> accountRepository.save(account);
    }

}