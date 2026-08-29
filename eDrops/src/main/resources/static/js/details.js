document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("details").forEach((details, index) => {
        const key = `details_${window.location.pathname}_${details.id || index}`;
        const savedState = sessionStorage.getItem(key);

        if (savedState !== null) {
            details.open = savedState === "true";
        }

        details.addEventListener("toggle", () => {
            sessionStorage.setItem(key, details.open);
        });
    });
});