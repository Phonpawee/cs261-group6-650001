package LoginReq;

public class LoginRequest {
    private String email;
    private String nationalId; //เลขบัตร

    
    public LoginRequest() {
    }

    
    public LoginRequest(String email, String nationalId) {
        this.email = email;
        this.nationalId = nationalId;
    }

    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
}
