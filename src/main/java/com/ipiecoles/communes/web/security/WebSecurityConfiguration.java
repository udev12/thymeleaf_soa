package com.ipiecoles.communes.web.security;

import com.ipiecoles.communes.web.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Annotation pour que Spring prnne en compte les éléments de configuration définis
@Configuration
// Annotation permettant d'activer la sécu pour notre appli web
@EnableWebSecurity
// Prise en compte des annotations de sécurité
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
// WebSecurityConfigurerAdapter : classe définissant un certain nombre de comportements par défaut et de fonctionnalités autour de la sécurité
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

//    //TODO implémenter notre propre service !!
//    private UserDetailsService userDetailsService;

    @Autowired
    public MyUserDetailsService userDetailsService;

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//    }

    // On redéfinit AuthenticationManagerBuilder

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
        auth
                //Service chargé d'effectuer les opérations d'authentification
                .userDetailsService(userDetailsService)
                //Définit l'algorithme de hâchage pour les mots de passe
                .passwordEncoder(passwordEncoder());
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                //Service chargé d'effectuer les opérations d'authentification
//                .userDetailsService(userDetailsService)
//                //Définit l'algorithme de hâchage pour les mots de passe
//                .passwordEncoder(passwordEncoder()); // le bean va être être récupéré
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Algo BCrypt
        return new BCryptPasswordEncoder();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        super.configure(http);
//        http.formLogin() // formulaire
//                // lorsqu'on va accéder à une page protégée vers où on redirige l'utilisateur pour qu'il puisse se connecter
//                .loginPage("/login")
//                // où va-t-on si la connexion échoue?
//        .failureUrl("/login?error=true")
//        // où va-t-on lorsque la oonnexion réusit?
//        .defaultSuccessUrl("/?successfulConnection=true")
//        // Définir le nom du paramètre contenant le com de l'utilisateur
//        .usernameParameter("username") // défaut : username
//        // Définir le nom du paramètre contenant le password
//        .passwordParameter("password") //Défaut : password
//        .and().logout()
//                .logoutUrl("/logout") // Défaut : /logout
//        .logoutSuccessUrl("/login?logout=true"); // Défaut /login?logout
//
//    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                //Activation de la connexion par formulaire HTML
//                .formLogin()
//                //Lorsque l'on va accéder à une page protégée, vers où on redirige
//                //l'utilisateur pour qu'il puisse se connecter
//                .loginPage("/login") //Défaut : /login
//                //Où va-t-on si la connexion échoue ?
//                .failureUrl("/login?error=true") //Défaut : /login?error
//                //Où va-t-on lorsque la connexion réussit ?
//                .defaultSuccessUrl("/?successfulConnection=true")// Pas de valeur par défaut
//                //Définir le nom du paramètre contenant le nom d'utilisateur
//                .usernameParameter("username")//Défaut : username
//                //Définir le nom du paramètre contenant le password
//                .passwordParameter("password")//Défaut : password
//                //Gestion de la déconnexion
//                .and().logout()
//                //Où va-t-on lorsque l'on souhaite se déconnecter ?
//                .logoutUrl("/logout") //Défaut : /logout
//                //Où va-t-on une fois la déconnexion effectuée
//                .logoutSuccessUrl("/login?logout=true"); //Défaut /login?logout
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                    //La page d'accueil / ...
//                    .antMatchers("/")
//                    //... est accessible à tous
//                    .permitAll()
//                    //Toutes les autres requêtes...
//                    .anyRequest()
//                    //... demandent à être authentifié
//                    .authenticated()
        http

                .authorizeRequests()
                //La page d'accueil / ...
                .antMatchers("/", "/register")
                //... est accessible à tous
                .permitAll()
                .antMatchers(HttpMethod.GET, "/communes/*") // url end points accessibles sans authentification
                .hasRole("ADMIN")
                //Toutes les autres requêtes...
                .anyRequest()
                //... demandent à être authentifié
                .authenticated()
                //Activation de la connexion par formulaire HTML
                .and().formLogin()
                    //Lorsque l'on va accéder à une page protégée, vers où on redirige
                    //l'utilisateur pour qu'il puisse se connecter
                    .loginPage("/login") //Défaut : /login
                    //Autoriser la page de login à tous
                    .permitAll() // à commenter avec la deuxième façon de configurer le login
                    //Où va-t-on si la connexion échoue ?
                    .failureUrl("/login?error=true") //Défaut : /login?error
                    //Où va-t-on lorsque la connexion réussit ?
                    .defaultSuccessUrl("/?successfulConnection=true")// Pas de valeur par défaut
                    //Définir le nom du paramètre contenant le nom d'utilisateur
                    .usernameParameter("username")//Défaut : username
                    //Définir le nom du paramètre contenant le password
                    .passwordParameter("password")//Défaut : password
                .and().logout() // déconnexion
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/") // on est redirigé vers la page d'accueil après la déconnexion
                    .invalidateHttpSession(true) // on invalide la session http
                    .deleteCookies("JSESSIONID"); // on supprime les cookies
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/webjars/**");// * => /webjars/test.js ** => //webjars/test/test/test.js
    }


}
