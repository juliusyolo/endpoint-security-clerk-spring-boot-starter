package com.juliusyolo.exception;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

/**
 * <p>
 * custom user authorization exception that extends {@link AccessDeniedException }
 * </p>
 *
 * @author julius.yolo
 * @version : UserAuthorizationException v0.1
 */
public class UserAuthorizationException extends AccessDeniedException{


    public UserAuthorizationException(String msg) {
        super(msg);
    }

    public UserAuthorizationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public static <T> Mono<T> error(String msg){
        return Mono.error(new UserAuthorizationException(msg));
    }


    public static <T> Mono<T> error(String msg, Throwable cause){
        return Mono.error(new UserAuthorizationException(msg, cause));
    }

}
