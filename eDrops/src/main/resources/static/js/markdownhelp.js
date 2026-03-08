const STORAGE_KEY = 'markdownHelpOpen';
const CONTAINER_ID = 'help-container';

function initMarkdownHelp() {
    console.log('initMarkdownHelp');
    const helpContainer = document.getElementById(CONTAINER_ID);
    if (!helpContainer) return;
    console.log('initMarkdownHelp helpContainer');
    const isPreviewActive = document.querySelector('.view') !== null;
    const shouldBeOpen = sessionStorage.getItem(STORAGE_KEY) === 'true';

    if (shouldBeOpen && !isPreviewActive) {
        helpContainer.style.display = 'block';
    } else if (isPreviewActive) {
        sessionStorage.setItem(STORAGE_KEY, 'false');
        helpContainer.style.display = 'none';
    }
}

function toggleMarkdownHelp() {
    console.log('toggleMarkdownHelp');
    const helpContainer = document.getElementById(CONTAINER_ID);
    if (!helpContainer) return;
    console.log('help container found');
    const isHidden = helpContainer.style.display === 'none' || helpContainer.style.display === '';

    if (isHidden) {
        helpContainer.style.display = 'block';
        sessionStorage.setItem(STORAGE_KEY, 'true');
    } else {
        helpContainer.style.display = 'none';
        sessionStorage.setItem(STORAGE_KEY, 'false');
    }
}

document.addEventListener('DOMContentLoaded', initMarkdownHelp);