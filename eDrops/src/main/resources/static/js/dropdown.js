document.addEventListener("DOMContentLoaded", function () {

    function updateDropdownItems() {
        let top = true
        document.querySelectorAll(".dropdown-item").forEach(function (item) {
            if (item.dataset.collapsible === "true") {
                top = false
            }
            if (!top) {
                item.classList.add("collapsible");
            }
        });
    }

    updateDropdownItems();

    document.querySelectorAll(".dropdown-item").forEach(function (item) {

        if (item.dataset.collapsible === "true") {
            item.addEventListener("click", function (event) {
                event.preventDefault(); // Prevent navigation
                item.classList.toggle("expandable")
                item.classList.add("collapsible")
                let nextElement = item.nextElementSibling;
                while (nextElement &&
                nextElement.classList.contains("dropdown-item") &&
                nextElement.dataset.collapsible === "false") {
                    nextElement.classList.toggle("collapsed");
                    nextElement.classList.add("collapsible");
                    nextElement = nextElement.nextElementSibling;
                }
            });
        }
    });
});