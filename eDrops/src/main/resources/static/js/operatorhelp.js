const OP_STORAGE_KEY = 'operatorHelpOpen';
const OP_CONTAINER_ID = 'operator-help-container';

function toggleOperatorHelp() {
    console.log("ReierAsk was here");
    const helpContainer = document.getElementById(OP_CONTAINER_ID);
    if (!helpContainer) return;
    const isHidden = helpContainer.style.display === 'none' || helpContainer.style.display === '';

    if (isHidden) {
        helpContainer.style.display = 'block';
        sessionStorage.setItem(OP_STORAGE_KEY, 'true');
    } else {
        helpContainer.style.display = 'none';
        sessionStorage.setItem(OP_STORAGE_KEY, 'false');
    }
}
