let events = document.currentScript.dataset.events;

document.addEventListener("DOMContentLoaded", function () {
    let topEl = document.getElementById("calendar-top");
    let bottomEl = document.getElementById("calendar-bottom");

    events = JSON.parse(events);

    const commonOptions = {
        height: "240px",
        headerToolbar: false,
        locale: "pl",
        dayHeaderFormat: { weekday: 'long', day: '2-digit', month: '2-digit' },
        eventTimeFormat: { hour: "2-digit", minute: "2-digit", hour12: false },
        events: events
    };

    function getMonday(d) {
        let date = new Date(d);
        let day = date.getDay();
        if (day === 0) {
            day = 7;
        }
        date.setDate(date.getDate() - day + 1);
        return date;
    }

    // Top calendar (Mon–Thu)
    const topCal = new FullCalendar.Calendar(topEl, {
        ...commonOptions,
        initialView: "dayGrid",
        visibleRange: function (currentDate) {
            let start = getMonday(currentDate);
            let end = new Date(start);
            end.setDate(start.getDate() + 3); // czwartek
            return { start, end };
        }
    });

    // Bottom calendar (Fri–Sun)
    const bottomCal = new FullCalendar.Calendar(bottomEl, {
        ...commonOptions,
        initialView: "dayGrid",
        visibleRange: function (currentDate) {
            let start = getMonday(currentDate);
            start.setDate(start.getDate() + 4);
            let end = new Date(start);
            end.setDate(start.getDate() + 2);
            return { start, end };
        }
    });

    topCal.render();
    bottomCal.render();
});