(function () {
    const counterEl = document.getElementById("counter");
    if (!counterEl) return;

    const script = document.currentScript;
    const targetDateStr = script.dataset.target;
    const startDateStr = script.dataset.start;

    if (!targetDateStr) {
        counterEl.innerText = "No target date";
        return;
    }

    if (!startDateStr) {
        counterEl.innerText = "No start date";
        return;
    }

    const targetDate = new Date(targetDateStr);
    const startDate = new Date(startDateStr);

    const totalDays =
        Math.max(1, Math.ceil((targetDate - startDate) / (1000 * 60 * 60 * 24)));

    const size = 200;
    const stroke = 18;
    const radius = (size - stroke) / 2;
    const circumference = 2 * Math.PI * radius;
    const padding = 6;
    const center = size / 2;
    const adjustedRadius = radius - padding;
    const adjustedCircumference = 2 * Math.PI * adjustedRadius;


    counterEl.innerHTML = `
<svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">

  <!-- Outer border -->
  <circle
    cx="${center}"
    cy="${center}"
    r="${adjustedRadius + stroke / 2}"
    fill="white"
    stroke="black"
    stroke-width="7"
  />

  <!-- Background track -->
  <circle
    cx="${center}"
    cy="${center}"
    r="${adjustedRadius}"
    fill="none"
    stroke="white"
    stroke-width="${stroke}"
  />

  <!-- Progress ring -->
  <circle
    id="progress-circle"
    cx="${center}"
    cy="${center}"
    r="${adjustedRadius}"
    fill="none"
    stroke="red"
    stroke-width="${stroke}"
    stroke-linecap="butt"
    stroke-dasharray="${adjustedCircumference}"
    stroke-dashoffset="${adjustedCircumference}"
    transform="rotate(-90 ${center} ${center})"
  />

  <text
    x="50%"
    y="46%"
    text-anchor="middle"
    font-size="55"
    font-family="sans-serif"
    fill="red"
    id="days-text"
    font-weight="bold"
  ></text>
  <text
    x="50%"
    y="57%"
    text-anchor="middle"
    font-size="20"
    font-family="sans-serif"
    fill="black"
    font-weight="bold"
  >Dni</text>
  <text
    x="50%"
    y="67%"
    text-anchor="middle"
    font-size="20"
    font-family="sans-serif"
    fill="black"
    font-weight="bold"
  >do Tajlandziej</text>
  <text
    x="50%"
    y="77%"
    text-anchor="middle"
    font-size="20"
    font-family="sans-serif"
    fill="black"
    font-weight="bold"
  >Przygody!</text>

</svg>
`;




    const progressCircle = document.getElementById("progress-circle");
    const daysText = document.getElementById("days-text");

    function update() {
        // const now = new Date("2026-01-15");
        const now = new Date();
        const remainingDays = Math.max(
            0,
            Math.ceil((targetDate - now) / (1000 * 60 * 60 * 24))
        );

        const progress =
            1 - remainingDays / totalDays;

        progressCircle.style.strokeDashoffset =
            circumference * (1 - progress);

        daysText.textContent = remainingDays;
    }

    update();
})();
