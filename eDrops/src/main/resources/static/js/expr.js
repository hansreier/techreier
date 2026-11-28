function selectExpr(select) {
    document.getElementById('action').value = 'select';
    select.form.submit();
}

function clearCalc() {
    const input = document.getElementById('calcInput');
    if (input) {
        input.value = '';
        input.focus();
    }
    const result = document.getElementById('calcResult');
    if (result) {
        result.innerHTML = '';
    }
}