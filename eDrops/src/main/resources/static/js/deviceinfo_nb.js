function deviceinfo() {
    return {
        screenWidth: screen.width,
        screenHeight: screen.height,
        viewportWidth: window.innerWidth,
        viewportHeight: window.innerHeight,
        devicePixelRatio: window.devicePixelRatio,
        userAgent: navigator.userAgent,
        deviceType: /Mobi|Android|iP(hone|ad)/.test(navigator.userAgent) ? "Mobil" : "Desktop/Nettbrett"
    };
}

function displayDeviceInfo() {
    const info = deviceinfo();
    const infoDiv = document.getElementById("deviceInfo");
    infoDiv.innerHTML = `
        <p><strong>Skjerm bredde:</strong> ${info.screenWidth}px</p>
        <p><strong>Skjerm høyde:</strong> ${info.screenHeight}px</p>
        <p><strong>Piksel rate:</strong> ${info.devicePixelRatio}</p>
        <p><strong>Vindu bredde:</strong> ${info.viewportWidth}px</p>
        <p><strong>Vindu høyde:</strong> ${info.viewportHeight}px</p>
        <p><strong>Enhet info:</strong><br> ${info.userAgent}</p>
        <p><strong>Enhet type:</strong> ${info.deviceType}</p>
    `;
}

document.addEventListener("DOMContentLoaded", displayDeviceInfo);
window.addEventListener("resize",displayDeviceInfo);