package my.utar.p5_irene_chow_ooi_ling_2204382;

public class PasswordModel {
    private int id;
    private String siteName;
    private String username;
    private String password;
    private String pinNumber;
    private String securityQuestion;
    private String securityAnswer;
    private String notes;

    public PasswordModel() {
    }

    public PasswordModel(int id, String siteName, String username, String password,
                         String pinNumber, String securityQuestion, String securityAnswer, String notes) {
        this.id = id;
        this.siteName = siteName;
        this.username = username;
        this.password = password;
        this.pinNumber = pinNumber;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.notes = notes;
    }

    public PasswordModel(String siteName, String username, String password,
                         String pinNumber, String securityQuestion, String securityAnswer, String notes) {
        this.siteName = siteName;
        this.username = username;
        this.password = password;
        this.pinNumber = pinNumber;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}