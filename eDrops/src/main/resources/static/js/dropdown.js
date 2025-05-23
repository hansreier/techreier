document.addEventListener("DOMContentLoaded", function () {
    // for enable / disable logging
    const isDev = window.location.hostname === 'localhost';
    const log = (...args) => isDev && console.log(...args);
    // Since the dropdown menus are dynamically generated, have to add class for css selectors to work.
    document.querySelectorAll(".dropdown").forEach(function (menu) {
        let top = true
        menu.querySelectorAll(".dropdown-item").forEach(function (item) {
            log("top")
            if (item.dataset.topic === "true") {
                top = false
            }
            if (!top) {
                item.classList.add("firstTopicDown")
                let expandable = sessionStorage.getItem(item.dataset.id + ".expandable")
                let collapsed = sessionStorage.getItem(item.dataset.id + ".collapsed")
                log("id", item.dataset.id,
                    "collapsed: ", sessionStorage.getItem(item.dataset.id + ".collapsed"),
                    "expandable: ", sessionStorage.getItem(item.dataset.id + ".expandable"))
                if (item.dataset.topic === "true") {
                    if (expandable === "true" || (expandable == null)) {
                        item.classList.add("expandable")
                    } else {
                        item.classList.remove("expandable")
                    }
                } else {
                    if (collapsed === "true" || (collapsed == null)) {
                        item.classList.add("collapsed")
                    } else {
                        item.classList.remove("collapsed")
                    }
                }
            }
        });
    });

    // listen to clicks on dropdown menu
    document.querySelectorAll(".dropdown-item[data-topic='true']")
        .forEach(function (item) {
            item.addEventListener("click", function (event) {
                event.preventDefault(); // Prevent navigation
                let expandable = item.classList.toggle("expandable")
                log("saving expandable in session storage", item.dataset.id, "expandable", expandable)
                sessionStorage.setItem(item.dataset.id + ".expandable", String(expandable))
                let nextElement = item.nextElementSibling;
                while (nextElement &&
                nextElement.classList.contains("dropdown-item") &&
                nextElement.dataset.topic === "false") {
                    let collapsed = nextElement.classList.toggle("collapsed");
                    nextElement.classList.add("topic");
                    log("saving collapsed in session storage", nextElement.dataset.id, "collapsed", collapsed)
                    sessionStorage.setItem(nextElement.dataset.id + ".collapsed", String(collapsed));
                    nextElement = nextElement.nextElementSibling;
                }
            });
        });
});