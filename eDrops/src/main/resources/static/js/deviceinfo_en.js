import { deviceinfo } from './deviceinfo.js';

function displayDeviceInfo() {
    const info = deviceinfo();
    const infoDiv = document.getElementById("deviceInfo");
    infoDiv.innerHTML = `
        <p><strong>Screen Width:</strong> ${info.screenWidth}px</p>
        <p><strong>Screen Height:</strong> ${info.screenHeight}px</p>
        <p><strong>Device Pixel Ratio:</strong> ${info.devicePixelRatio}</p>
        <p><strong>Viewport Width:</strong> ${info.viewportWidth}px</p>
        <p><strong>Viewport Height:</strong> ${info.viewportHeight}px</p>
        <p><strong>Font size:</strong> ${info.fontSize}</p>
        <p><strong>Font type:</strong> ${info.fontFamily}</p>
        <p><strong>Language:</strong> ${info.language}</p>
        <p><strong>Device info:</strong><br> ${info.userAgent}</p>
    `;
}

document.addEventListener("DOMContentLoaded", displayDeviceInfo);
window.addEventListener("resize", displayDeviceInfo);
