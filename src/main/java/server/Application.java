package server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import server.model.Account;
import server.model.Item;
import server.model.Wishlist;
import server.persistence.AccountRepository;
import server.persistence.ItemRepository;
import server.persistence.WishlistRepository;

import java.util.Arrays;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository,
                           WishlistRepository wishlistRepository,
                           ItemRepository itemRepository) {
        return (evt) -> Arrays.asList(
                "Anton,Kamill,Lola,Liza".split(","))
                .forEach(
                        a -> {
                            Account account = accountRepository.save(new Account(a));
                            Wishlist wishlist = new Wishlist("New Year " + a, account);
                            wishlistRepository.save(wishlist);
                            itemRepository.save(new Item("Book", wishlist));
                            itemRepository.save(new Item("Car", wishlist));

                        });
    }

}