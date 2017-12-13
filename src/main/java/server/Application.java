package server;

import com.google.gson.Gson;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import server.model.Account;
import server.model.Item;
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
    public VkApiClient vkApiClient() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        return new VkApiClient(transportClient, new Gson(), 3);
    }

    @Bean
    public Mapper classMapper() {
        return new Mapper();
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository,
                           WishlistRepository wishlistRepository,
                           ItemRepository itemRepository) {
        List<Account> list = new ArrayList<>();
        Arrays.asList("Anton,Kamill,Lola,Liza".split(",")).forEach(s -> {
            Account account = accountRepository.save(new Account(s));
            list.add(account);
            account.setRegistered(true);
            Wishlist wishlist = new Wishlist("New Year " + s, account);
            wishlistRepository.save(wishlist);
            itemRepository.save(new Item("Book", wishlist));
            itemRepository.save(new Item("Car", wishlist));
        });
        list.get(0).getFriends().add(list.get(1));
        list.get(0).getFriends().add(list.get(2));
        list.get(0).getFriends().add(list.get(3));
        list.get(1).getFriends().add(list.get(0));
        list.get(2).getFriends().add(list.get(0));
        list.get(3).getFriends().add(list.get(0));
        list.get(3).getFriends().add(list.get(2));
        list.get(2).getFriends().add(list.get(3));
        accountRepository.save(list);
        return (evt) -> list.toArray();

//        return (evt) -> Arrays.asList(
//                "Anton,Kamill,Lola,Liza".split(","))
//                .forEach(
//                        a -> {
//                            Account account = accountRepository.save(new Account(a));
//                            account.setRegistered(true);
//                            Wishlist wishlist = new Wishlist("New Year " + a, account);
//                            wishlistRepository.save(wishlist);
//                            itemRepository.save(new Item("Book", wishlist));
//                            itemRepository.save(new Item("Car", wishlist));
//                        });
    }

}