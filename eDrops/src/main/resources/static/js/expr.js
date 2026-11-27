function loadexpr(select) {
    const selectedOption = select.selectedOptions[0];
    const input = document.getElementById('calcInput');
    input.value = selectedOption?.dataset.expr || '';

    const details = document.getElementById('expr-details');
    const aboutDiv = document.getElementById('expr-about');
    const aboutHtml = selectedOption?.dataset.about || '';

    if (aboutDiv) {
        aboutDiv.innerHTML = aboutHtml;
    }
    if (details) {
        details.style.display = aboutHtml ? '' : 'none';
    }
}

function clearResult() {
    const result = document.getElementById('calcResult');
    if (result) result.innerHTML = '';
}

function clearCalc() {
    const select = document.getElementById('expr');
    select.selectedIndex = 0;
    loadexpr(select);
    clearResult();
    document.getElementById('calcInput').focus();
}

document.addEventListener('DOMContentLoaded', () => {
    const select = document.getElementById('expr');
    if (select) {
        loadexpr(select);
        select.addEventListener('change', clearResult);
    }
});