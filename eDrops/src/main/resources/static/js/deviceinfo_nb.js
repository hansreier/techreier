import { deviceinfo } from './deviceinfo.js';

function displayDeviceInfo() {
    const info = deviceinfo();
    const infoDiv = document.getElementById("deviceInfo");
    infoDiv.innerHTML = `
        <p><strong>Skjerm bredde:</strong> ${info.screenWidth}px</p>
        <p><strong>Skjerm høyde:</strong> ${info.screenHeight}px</p>
        <p><strong>Piksel rate:</strong> ${info.devicePixelRatio}</p>
        <p><strong>Vindu bredde:</strong> ${info.viewportWidth}px</p>
        <p><strong>Vindu høyde:</strong> ${info.viewportHeight}px</p>
        <p><strong>Font størrelse:</strong> ${info.fontSize}</p>
        <p><strong>Font type:</strong> ${info.fontFamily}</p>
        <p><strong>Tidssone:</strong> ${info.timeZone}</p>
        <p><strong>Tid:</strong> ${info.currentTime}</p>
        <p><strong>Enhet info:</strong><br> ${info.userAgent}</p>
    `;
}

document.addEventListener("DOMContentLoaded", displayDeviceInfo);
window.addEventListener("resize", displayDeviceInfo);
