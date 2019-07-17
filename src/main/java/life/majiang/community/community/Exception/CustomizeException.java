package life.majiang.community.community.Exception;

public class CustomizeException extends RuntimeException{
    private String message;
    private int code;

//相当于一个异常的bean
    @Override
    public String getMessage() {
        return message;
    }

    public int getCode(){return code;}

    public CustomizeException(ICustomizeErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();

    }
    public CustomizeException(String message,int code) {
        this.message = message;
        this.code= code;
    }

}
