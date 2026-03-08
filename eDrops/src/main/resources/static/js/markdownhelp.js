const STORAGE_KEY = 'markdownHelpOpen';
const CONTAINER_ID = 'help-container';

function toggleMarkdownHelp() {
    const helpContainer = document.getElementById(CONTAINER_ID);
    if (!helpContainer) return;
    const isHidden = helpContainer.style.display === 'none' || helpContainer.style.display === '';

    if (isHidden) {
        helpContainer.style.display = 'block';
        sessionStorage.setItem(STORAGE_KEY, 'true');
    } else {
        helpContainer.style.display = 'none';
        sessionStorage.setItem(STORAGE_KEY, 'false');
    }
}
