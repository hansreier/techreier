window.addEventListener('load', function() {
    const storedTimeZone = sessionStorage.getItem('timezone');

    if (!storedTimeZone) {
        const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;

        fetch('/timezone', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ timezone: timezone })
        })
            .then(response => {
                sessionStorage.setItem('timezone', timezone);
            })
            .catch(error => {
                console.error('Error setting timezone:', error);
            });
    }
});