document.addEventListener("DOMContentLoaded", function () {

    // Since the dropdown menus are dynamically generated, have to add class for css selectors to work.
    let top = true
    document.querySelectorAll(".dropdown-item").forEach(function (item) {
        if (item.dataset.collapsible === "true") {
            top = false
        }
        if (!top) {
            item.classList.add("collapsible")
            if (item.dataset.collapsible === "true") {
                item.classList.toggle("expandable")
            } else {
                item.classList.toggle("collapsed")
            }
        }
    });

    //listen to clicks on dropdown menu
    document.querySelectorAll(".dropdown-item").forEach(function (item) {

        if (item.dataset.collapsible === "true") {
            item.addEventListener("click", function (event) {
                event.preventDefault(); // Prevent navigation
                item.classList.toggle("expandable")
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