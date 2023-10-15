package com.ShopMe.ExceptionHandler;


import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice// we can also extends ResponseEntityExceptionHandler class and then override handelMethodArgumentNotValidException
public class GlobelExceptionHandler  {

    //if ResourceNotFoundException ayega toh ye message Chal jayega aur exception handel ho jayega with proper Message
  /*  @ExceptionHandler(ResourceNotFoundException.class) // this is Custom Exception
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        String message = ex.getMessage();

        ApiResponse apiResponse = new ApiResponse(message,false);

        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    // Hibernate Validation se related koi Exception anne pr ye method chalega aur proper Message ke saath Handel ho jayega
    @ExceptionHandler(MethodArgumentNotValidException.class) // provided by Spring
    public ResponseEntity<Map<String, String>> handelMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String,String> resp = new HashMap<>();

        // It will give all error in every field
        ex.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError) error).getField();

            String message = error.getDefaultMessage();

            resp.put(fieldName, message); // konsi field pe konsa error hai
        });

        return new ResponseEntity<Map<String, String>>(resp,HttpStatus.BAD_REQUEST);
    }
*/

}
