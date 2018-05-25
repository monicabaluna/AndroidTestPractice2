package ro.pub.cs.systems.eim.practicaltest02;

/**
 * Created by monica on 25.05.2018.
 */

public class WeatherForecastInformation {
    private String temperature;
    private String windSpeed;
    private String condition;
    private String pressure;
    private String humidity;

    public WeatherForecastInformation() {
        this.temperature = null;
        this.windSpeed = null;
        this.condition = null;
        this.pressure = null;
        this.humidity = null;
    }

    public WeatherForecastInformation(String temperature, String windSpeed, String condition, String pressure, String humidity) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getCondition() {
        return condition;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getPressure() {
        return pressure;
    }

    @Override
    public String toString() {
        return "WeatherForecastInformation{" +
                "temperature='" + temperature + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", condition='" + condition + '\'' +
                ", pressure='" + pressure + '\'' +
                ", humidity='" + humidity + '\'' +
                '}';
    }
}
