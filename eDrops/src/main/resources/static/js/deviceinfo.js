export function deviceinfo() {
    const paragraph = document.querySelector("p")
    return {
        screenWidth: screen.width,
        screenHeight: screen.height,
        viewportWidth: window.innerWidth,
        viewportHeight: window.innerHeight,
        devicePixelRatio: window.devicePixelRatio,
        userAgent: navigator.userAgent,
        language: navigator.language,
        fontSize: window.getComputedStyle(paragraph).getPropertyValue("font-size"),
        fontFamily: window.getComputedStyle(paragraph).getPropertyValue("font-family")
    };
}

document.addEventListener("DOMContentLoaded", function () {
    const deviceInfo = document.getElementById("deviceInfo");
    if (deviceInfo) {
        const workElement = document.getElementById("work")
        if (workElement) {
            workElement.classList.add("has-device-info");
        }
    }
});