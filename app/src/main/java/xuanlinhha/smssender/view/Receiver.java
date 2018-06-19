package xuanlinhha.smssender.view;

public class Receiver {
    public enum Status {Fresh, Sent, Delivered, Fail}

    private String no;
    private Status status;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
