document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".dropdown-item").forEach(function (item) {
        if (item.dataset.collapsible === "true") {
            item.addEventListener("click", function (event) {
                event.preventDefault(); // Prevent navigation
                let nextElement = item.nextElementSibling;
                while (nextElement &&
                        nextElement.classList.contains("dropdown-item") &&
                        nextElement.dataset.collapsible === "false")  {
                    nextElement.classList.toggle("collapsed");
                    nextElement = nextElement.nextElementSibling;
                }
            });
        }
    });
});