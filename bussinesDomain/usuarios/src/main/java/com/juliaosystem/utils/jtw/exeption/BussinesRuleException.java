
package com.juliaosystem.utils.jtw.exeption;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 *
 * @author daniel
 */
@Getter
@Setter
public class BussinesRuleException extends Exception {

    private long id;
    private String code;   
    private HttpStatus httpStatus;
    
    public BussinesRuleException(long id, String code, String message, HttpStatus httpStatus) {
        super(message);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BussinesRuleException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
    
    public BussinesRuleException(String message, Throwable cause) {
        super(message, cause);
    }


    
}
