package com.juliusyolo.exception;


import org.springframework.security.core.AuthenticationException;
import reactor.core.publisher.Mono;

/**
 * <p>
 * UserAuthenticationException
 * </p>
 *
 * @author julius.yolo
 * @version : UserAuthenticationException v0.1
 */
public class UserAuthenticationException extends AuthenticationException {

    public UserAuthenticationException(String msg) {
        super(msg);
    }

    public UserAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public static <T> Mono<T> error(String msg){
        return Mono.error(new UserAuthenticationException(msg));
    }


    public static <T> Mono<T> error(String msg, Throwable cause){
        return Mono.error(new UserAuthenticationException(msg, cause));
    }
}
