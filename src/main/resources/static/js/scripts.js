function submitForm() {
    const form = document.getElementById('tranForm');
    const formData = new FormData(form);

    const data = {
        targetService: formData.get('targetService'),
        dbProfile: formData.get('dbProfile')
    };

    fetch('/doTran', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(error => {
                throw new Error(error.message);
            });
        }
        return response.json();
    })
    .then(result => {
        document.getElementById('result').innerText = `Response: ${result.message}`;
    })
    .catch(error => {
        document.getElementById('result').innerText = `Error: ${error.message}`;
        console.error('Error:', error);
    });
}


function showAiServiceDevData() {
    fetch('/api/ai-chap/dev')
        .then(response => response.json())
        .then(data => {
            let dataTable = document.getElementById('dev-data-table').getElementsByTagName('tbody')[0];
            createAiChapTable(data,dataTable);
        })
        .catch(error => console.error('Error loading data:', error));
}


function showAiServiceRealData() {
    fetch('/api/ai-chap/real')
        .then(response => response.json())
        .then(data => {
            let dataTable = document.getElementById('real-data-table').getElementsByTagName('tbody')[0];
            createAiChapTable(data,dataTable);

        })
        .catch(error => console.error('Error loading data:', error));
}

function createAiChapTable(data, dataTable){
    dataTable.innerHTML = '';  // 기존 데이터를 제거

    data.forEach(item => {
        let row = dataTable.insertRow();

        let seqCell = row.insertCell(0);
        let titleCell = row.insertCell(1);
        let typeCell = row.insertCell(2);

        seqCell.textContent = item.aiChapSeq;
        titleCell.textContent = item.aiChapTitle;
        typeCell.textContent = item.aiChapType;
    });
}