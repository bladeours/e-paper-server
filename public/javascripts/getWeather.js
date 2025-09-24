const scriptEl = document.currentScript;
const icons = {
    sunny: scriptEl.dataset.sunny,
    cloudy: scriptEl.dataset.cloudy,
    rainy: scriptEl.dataset.rainy,
    snowy: scriptEl.dataset.snowy,
    thunderstorm: scriptEl.dataset.thunderstorm
};

function getWeatherIcon(code, isCurrent = false) {
    let size = isCurrent ? 50 : 30;
    if ([0].includes(code)) return `<img src='${icons.sunny}' alt="Sunny" class="forecast-icon" style="width:${size}px;height:${size}px">`;
    if ([1,2,3].includes(code)) return `<img src='${icons.cloudy}' alt="Cloudy" class="forecast-icon" style="width:${size}px;height:${size}px">`;
    if ([51,53,55,56,57,61,63,65,80,81,82].includes(code)) return `<img src='${icons.rainy}' alt="Rainy" class="forecast-icon" style="width:${size}px;height:${size}px">`;
    if ([71,73,75,77,85,86].includes(code)) return `<img src='${icons.snowy}' alt="Snowy" class="forecast-icon" style="width:${size}px;height:${size}px">`;
    if ([95,96,99].includes(code)) return `<img src='${icons.thunderstorm}' alt="Thunderstorm" class="forecast-icon" style="width:${size}px;height:${size}px">`;
    return "❓";
}

let lat = document.currentScript.dataset.lat;
let long = document.currentScript.dataset.long;
if(lat === "{{LAT}}") {
    lat = "52.52"
    long = "52.52"
}

fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${long}&hourly=temperature_2m,weathercode&current_weather=true&forecast_days=1&timezone=auto`)
    .then(res => res.json())
    .then(data => {
        const hours = [8, 12, 16, 20];

        const currentTemp = data.current_weather.temperature;
        const currentCode = data.current_weather.weathercode;
        const currentIcon = getWeatherIcon(currentCode, true);

        let html = `<h2 class="current-temp">${currentTemp}°C <span class="icon">${currentIcon}</span></h2>`;

        data.hourly.time.forEach((t, i) => {
            let hour = new Date(t).getHours();
            if (hours.includes(hour)) {
                const temp = data.hourly.temperature_2m[i];
                const code = data.hourly.weathercode[i];
                const icon = getWeatherIcon(code);
                html += `
          <div class="weather-entry">
              <span class="hour">${hour}:00</span>
              <span class="temp">${temp}°C</span>
              <span class="icon">${icon}</span>
          </div>
        `;
            }
        });

        document.getElementById("weather").innerHTML = html;
    });