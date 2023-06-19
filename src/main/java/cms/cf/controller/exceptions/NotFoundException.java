package cms.cf.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(Exception e)
    {
        super(e);
    }
    
    public NotFoundException(String msg) {
        super(msg);
    }
    
    public NotFoundException(String msg, Exception e)
    {
        super(msg,e);
    }
}