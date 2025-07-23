package com.example.bankmanagement.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error"); // Your error.html template
        mav.addObject("status", HttpStatus.NOT_FOUND.value());
        mav.addObject("error", "Not Found");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ModelAndView handleInvalidRequestException(InvalidRequestException ex) {
        log.error("Invalid request: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());
        mav.addObject("error", "Bad Request");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ModelAndView handleInsufficientBalanceException(InsufficientBalanceException ex) {
        log.error("Insufficient balance: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());
        mav.addObject("error", "Operation Failed");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ModelAndView handleUnauthorizedOperationException(UnauthorizedOperationException ex) {
        log.warn("Unauthorized operation attempt: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", HttpStatus.FORBIDDEN.value());
        mav.addObject("error", "Access Denied");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", HttpStatus.FORBIDDEN.value());
        mav.addObject("error", "Access Denied");
        mav.addObject("message", "You do not have permission to access this resource.");
        return mav;
    }


    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: ", ex); // Log the full stack trace
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        mav.addObject("error", "Internal Server Error");
        mav.addObject("message", "An unexpected error occurred. Please try again later.");
        return mav;
    }
}