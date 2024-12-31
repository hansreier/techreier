function deviceinfo() {
    return {
        screenWidth: screen.width,
        screenHeight: screen.height,
        viewportWidth: window.innerWidth,
        viewportHeight: window.innerHeight,
        devicePixelRatio: window.devicePixelRatio,
        userAgent: navigator.userAgent,
        deviceType: /Mobi|Android|iP(hone|ad)/.test(navigator.userAgent) ? "Mobile" : "Desktop/Tablet"
    };
}

function displayDeviceInfo() {
    const info = deviceinfo();
    const infoDiv = document.getElementById("deviceInfo");
    infoDiv.innerHTML = `
        <p><strong>Screen Width:</strong> ${info.screenWidth}px</p>
        <p><strong>Screen Height:</strong> ${info.screenHeight}px</p>
        <p><strong>Device Pixel Ratio:</strong> ${info.devicePixelRatio}</p>
        <p><strong>Viewport Width:</strong> ${info.viewportWidth}px</p>
        <p><strong>Viewport Height:</strong> ${info.viewportHeight}px</p>
        <p><strong>User Agent:</strong><br> ${info.userAgent}</p>
        <p><strong>Device Type:</strong> ${info.deviceType}</p>
    `;
}

document.addEventListener("DOMContentLoaded", displayDeviceInfo);
window.addEventListener("resize",displayDeviceInfo);

document.addEventListener("DOMContentLoaded", function () {
    const deviceInfo = document.getElementById("deviceInfo");
    if (deviceInfo) {
        const workElement = document.getElementById("work")
        if (workElement) {
            workElement.classList.add("has-device-info");
        }
    }
});