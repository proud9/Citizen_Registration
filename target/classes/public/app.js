const $ = s => document.querySelector(s);
const form = $('#recordForm');

$('#type').addEventListener('change', () => {
    const birth = $('#type').value === 'birth';
    $('#birthFields').hidden = !birth;
    $('#nrcFields').hidden = birth;
    form.elements.placeOfBirth.required = birth;
    form.elements.parentName.required = birth;
    form.elements.applicationStatus.required = !birth;
});

async function request(url, opts = {}) {
    const r = await fetch(url, {
        ...opts,
        headers: { 'Content-Type': 'application/json', ...(opts.headers || {}) }
    });
    if (!r.ok) {
        let e = {};
        try { e = await r.json(); } catch {}
        throw Error(e.error || `Request failed (${r.status})`);
    }
    return r.status === 204 ? null : r.json();
}

function esc(s) {
    return String(s ?? '').replace(/[&<>"']/g, c => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}

async function refresh() {
    try {
        const records = await request('/api/records');
        const statuses = ['PENDING', 'PROCESSING', 'COMPLETED', 'REJECTED'];
        $('#list').innerHTML = records.length ? records.map(r => {
            const type = r.recordType || '';
            const isNrc = type === 'NRC_APPLICATION';
            const statusOptions = isNrc
                ? statuses.map(s => `<option value="${s}" ${s === r.applicationStatus ? 'selected' : ''}>${s}</option>`).join('')
                : '';
            return `<article class="card">
                <div>
                    <span class="tag">${esc(type.replaceAll('_', ' '))}</span>
                    <h3>${esc(r.fullName)}</h3>
                    <div class="meta">
                        Ref: ${esc(r.id)}<br>
                        DOB: ${esc(r.dateOfBirth)} · ${esc(r.province)}<br>
                        ${type === 'BIRTH'
                            ? `Born at ${esc(r.placeOfBirth)} · Parent: ${esc(r.parentName)}`
                            : `Status (PUT update): <select class="status-select" data-id="${esc(r.id)}">${statusOptions}</select>`
                        }
                    </div>
                </div>
                <button class="delete" data-id="${esc(r.id)}">Delete</button>
            </article>`;
        }).join('') : '<p class="meta">No records yet. Add a synthetic record to begin.</p>';

        $('#list').querySelectorAll('.delete').forEach(b => b.onclick = async () => {
            try {
                await request('/api/records/' + encodeURIComponent(b.dataset.id), { method: 'DELETE' });
                await refresh();
            } catch (e) {
                alert(e.message);
            }
        });

        $('#list').querySelectorAll('.status-select').forEach(sel => sel.onchange = async () => {
            try {
                const r = records.find(x => x.id === sel.dataset.id);
                if (!r) return;
                const updated = { ...r, applicationStatus: sel.value };
                await request('/api/nrc-applications/' + encodeURIComponent(sel.dataset.id), {
                    method: 'PUT',
                    body: JSON.stringify(updated)
                });
                await refresh();
            } catch (e) {
                alert(e.message);
                await refresh();
            }
        });
    } catch (e) {
        $('#list').textContent = e.message;
    }
}

form.addEventListener('submit', async e => {
    e.preventDefault();
    $('#message').textContent = '';
    const d = Object.fromEntries(new FormData(form));
    const birth = $('#type').value === 'birth';
    try {
        await request(birth ? '/api/birth-records' : '/api/nrc-applications', {
            method: 'POST',
            body: JSON.stringify(d)
        });
        form.reset();
        $('#type').dispatchEvent(new Event('change'));
        $('#message').textContent = 'Record created.';
        await refresh();
    } catch (err) {
        $('#message').textContent = err.message;
    }
});

$('#refresh').onclick = refresh;

$('#sample').onclick = async () => {
    try {
        await request('/api/birth-records', {
            method: 'POST',
            body: JSON.stringify({
                fullName: 'Demo Person A',
                dateOfBirth: '2000-01-15',
                province: 'Central',
                placeOfBirth: 'Demo Town',
                parentName: 'Demo Parent A'
            })
        });
        await request('/api/nrc-applications', {
            method: 'POST',
            body: JSON.stringify({
                fullName: 'Demo Person B',
                dateOfBirth: '1999-05-20',
                province: 'Copperbelt',
                applicationStatus: 'PENDING'
            })
        });
        await refresh();
    } catch (e) {
        alert(e.message);
    }
};

$('#type').dispatchEvent(new Event('change'));
refresh();
