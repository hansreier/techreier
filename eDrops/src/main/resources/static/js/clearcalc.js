function clearCalc() {
    const input = document.getElementById('calcInput');
    input.value = '';
    input.focus();
    const result = document.getElementById('calcResult');
    if (result) result.innerHTML = '';
}