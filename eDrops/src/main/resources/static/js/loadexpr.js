function loadexpr(select) {
    const selectedOption = select.selectedOptions[0];
    document.getElementById('calcInput').value = selectedOption?.dataset.expr || '';
}