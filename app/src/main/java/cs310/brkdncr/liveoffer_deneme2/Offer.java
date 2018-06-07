package cs310.brkdncr.liveoffer_deneme2;

/**
 * Created by brkdn on 28/04/2016.
 */
public class Offer {
    private String title;
    private String description;
    private String companyName;
    private String address;
    private double latitude;
    private double longitude;

    public Offer()
    {
    }

    public Offer(String title, String description, String companyName, String address, double latitude, double longitude)
    {
        this.title = title;
        this.description = description;
        this.companyName = companyName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getCompanyName()
    {
        return this.companyName;
    }

    public String getAddress()
    {
        return this.address;
    }

    public double[] getCoordinates()
    {
        double [] coordinates = new double[2];
        coordinates[0] = this.latitude;
        coordinates[1]= this.longitude;
        return coordinates;
    }
}
