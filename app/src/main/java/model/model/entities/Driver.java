package model.model.entities;

public class Driver {
    /**
     * fields
     */
    private String lastName;
    private String firstName;
    private Long id;
    private Long phoneNumber;
    private String emailAddress;
    private Long creditCard;
    private String password;

    /**
     *
     * @param lastName
     * @param firstName
     * @param id
     * @param phoneNumber
     * @param emailAddress
     * @param creditCard
     */
    public Driver(String lastName, String firstName, Long id, Long phoneNumber, String emailAddress,String password, Long creditCard) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.password = password;
        this.creditCard = creditCard;
    }

    /**
     *
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public Long getPhoneNumber() {
        return phoneNumber;
    }

    /**
     *
     * @param phoneNumber
     */
    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     *
     * @return
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     *
     * @param emailAddress
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     *
     * @return
     */
    public Long getCreditCard() {
        return creditCard;
    }

    /**
     *
     * @param creditCard
     */
    public void setCreditCard(Long creditCard) {
        this.creditCard = creditCard;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
