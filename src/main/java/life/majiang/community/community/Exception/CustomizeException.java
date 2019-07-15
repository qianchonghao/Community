package life.majiang.community.community.Exception;

public class CustomizeException extends RuntimeException{
    private String message;
//相当于一个异常的bean
    @Override
    public String getMessage() {
        return message;
    }

    public CustomizeException(ICustomizeErrorCode errorCode) {
        this.message = errorCode.getMessage();

    }
    public CustomizeException(String message) {
        this.message = message;

    }

}
