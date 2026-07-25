document.addEventListener("DOMContentLoaded", () => {
    const details = document.getElementById("post-details");
    if (!details) return;

    const savedState = sessionStorage.getItem("detailsOpen");
    if (savedState === "true") {
        details.open = true;
    } else if (savedState === "false") {
        details.open = false;
    }

    details.addEventListener("toggle", () => {
        sessionStorage.setItem("detailsOpen", details.open);
    });
});