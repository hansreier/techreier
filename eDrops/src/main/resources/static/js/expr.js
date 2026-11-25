function loadexpr(select) {
    const selectedOption = select.selectedOptions[0];
    const input = document.getElementById('calcInput');
    input.value = selectedOption?.dataset.expr || '';
    const result = document.getElementById('calcResult');
    if (result) result.innerHTML = '';
}

function clearCalc() {
    const select = document.getElementById('expr');
    select.selectedIndex = 0;
    loadexpr(select);
    document.getElementById('calcInput').focus();
}