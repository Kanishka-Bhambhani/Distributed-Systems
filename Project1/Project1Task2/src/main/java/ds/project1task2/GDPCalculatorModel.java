/**
 * Name : Kanishka Bhambhani
 * Andrew Id: kbhambha
 */
package ds.project1task2;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * class GDPCalculatorModel
 * The class acts as the Model in the MVC architecture.
 * It is used to fetch the data from the websites and clean the data
 */
public class GDPCalculatorModel {

    /**
     * function doSearch
     * The function is used to scrape the data from 3 different websites.
     * Each website's data is matched with the country name and then recorded
     * The weighted sum and expected weighted sum are calculated in this function
     * The img of the flag is scrapped
     * The function used jsoup to scrape the data from the websites
     * @param inputCountry The name of the country selected by the user
     * @return the list of data required to present to the user
     * @throws IOException to check if there are no exception while recieving or parsing the data
     */
    public ArrayList<String> doSearch(String inputCountry)
            throws IOException {
        // Scraping the world population data. Only for top 20 countries
        long population = 0;
        long gdp = 0;

        String population_string = "";
        String gdp_string = "";

        String url = "https://www.worldometers.info/gdp/gdp-by-country/";
        Document doc = Jsoup.connect(url).get();
        Element tableElement = doc.select("table").first();

        Elements tableRowElements = tableElement.select(":not(thead) tr");
        for (int i = 0; i < 21; i++) {
            Element row = tableRowElements.get(i);
            //System.out.println("row");
            Elements rowItems = row.select("td");
            for (int j = 0; j < rowItems.size(); j++) {
                if(inputCountry.equals(rowItems.get(1).text()))
                {
                    population_string = rowItems.get(5).text();
                    population = Long.parseLong(rowItems.get(5).text().replace(",",""));
                    gdp_string = rowItems.get(2).text();
                    gdp = Long.parseLong(rowItems.get(2).text().replace("$","")
                            .replace(",",""));
                }
            }
        }

        //Scraping the data for medals received by countries during olympics
        String gold = "";
        String silver = "";
        String bronze = "";

        String inputCountryString = inputCountry;
        if(inputCountry.equals("Russia"))
        {
            inputCountryString = "ROC";
        }
        else if(inputCountry.equals("United Kingdom"))
        {
            inputCountryString = "Great Britain";
        }
        else if(inputCountry.equals("South Korea"))
        {
            inputCountryString = "Republic of Korea";
        }
        else if(inputCountry.equals("United States"))
        {
            inputCountryString = "United States of America";
        }
        else if(inputCountry.equals("China"))
        {
            inputCountryString = "People's Republic of China";
        }

        String medalUrl = "https://olympics.com/tokyo-2020/olympic-games/en/results/all-sports/medal-standings.htm";
        Document medalDoc = Jsoup.connect(medalUrl).get();

        Element tableElementMedal = medalDoc.select("table").first();

        Elements tableRowElementsMedal = tableElementMedal.select(":not(thead) tr");

        for (int i = 0; i < tableRowElementsMedal.size(); i++) {
            Element row = tableRowElementsMedal.get(i);
            Elements rowItems = row.select("td");
            for (int j = 0; j < rowItems.size(); j++) {
                if(inputCountryString.equals(rowItems.get(1).text()))
                {
                    gold = rowItems.get(2).text();
                    silver = rowItems.get(3).text();
                    bronze = rowItems.get(4).text();
                }
            }
        }

        //Calculating weighted sum and expected medal count
        int weighted_sum = (3* (Integer.parseInt(gold))) + (2*(Integer.parseInt(silver))) + Integer.parseInt(bronze);
        double expected_medal_count = 0.1*(Math.pow(((population/1000000)*Math.pow((gdp/1000000000),2.0)),1.0/3.0));

        String inputCountryFlag = inputCountry;
        if(inputCountry.equals("United States"))
        {
            inputCountryFlag = "USA";
        }
        else if(inputCountry.equals("United Kingdom"))
        {
            inputCountryFlag = "United-Kingdom";
        }
        else if(inputCountry.equals("South Korea"))
        {
            inputCountryFlag = "South-Korea";
        }
        else if(inputCountry.equals("Saudi Arabia"))
        {
            inputCountryFlag = "Saudi-Arabia";
        }

        //Scraping the data for the flag of the country
        String flagUrl = "https://commons.wikimedia.org/wiki/Animated_GIF_flags";
        Document flagDoc = Jsoup.connect(flagUrl).get();

        Elements imageElements = flagDoc.select(".gallerybox");

        Elements tableRowElementsFlag = imageElements.select("img");

        //iterate over each image
        String strImageURL = "";
        for(int i =0; i< tableRowElementsFlag.size(); i++) {
            Element row = tableRowElementsFlag.get(i);
            if(row.attr("abs:src").contains(inputCountryFlag))
            {
                strImageURL = row.attr("abs:src");
            }
        }

        ArrayList<String> returnList = new ArrayList<>();
        returnList.add(population_string);
        returnList.add(gdp_string);
        returnList.add(gold);
        returnList.add(silver);
        returnList.add(bronze);
        returnList.add(String.valueOf(weighted_sum));
        returnList.add(String.valueOf(expected_medal_count));
        returnList.add(strImageURL);

        //Returning a list of data to the user
        return returnList;
    }
}

