document.addEventListener("DOMContentLoaded", () => {
    const details = document.getElementById("post-details");
    if (!details) return;

    console.log("reierAsk: Element funnet, open-status er nå:", details.open);
    console.log("reierAsk: sessionStorage 'detailsOpen' er:", sessionStorage.getItem("detailsOpen"));

    const savedState = sessionStorage.getItem("detailsOpen");
    if (savedState === "true") {
        console.log("reierAsk: Setter details.open = true basert på sessionStorage");
        details.open = true;
    } else if (savedState === "false") {
        console.log("reierAsk: Setter details.open = false basert på sessionStorage");
        details.open = false;
    }

    console.log("reierAsk: Legger til toggle-lyttefunksjon");
    details.addEventListener("toggle", () => {
        console.log("reierAsk: Toggle-hendelse trigget! Ny open-status:", details.open);
        sessionStorage.setItem("detailsOpen", details.open);
    });
});