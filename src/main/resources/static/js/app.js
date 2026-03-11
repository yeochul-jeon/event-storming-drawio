document.addEventListener('DOMContentLoaded', () => {
    const dropZone = document.getElementById('drop-zone');
    const fileInput = document.getElementById('file-input');
    const previewContainer = document.getElementById('preview-container');
    const previewImage = document.getElementById('preview-image');
    const removeBtn = document.getElementById('remove-image');
    const analyzeBtn = document.getElementById('analyze-btn');
    const loading = document.getElementById('loading');
    const resultSection = document.getElementById('result-section');
    const errorSection = document.getElementById('error-section');

    let selectedFile = null;

    // 드래그&드롭
    dropZone.addEventListener('click', () => fileInput.click());
    dropZone.addEventListener('dragover', e => {
        e.preventDefault();
        dropZone.classList.add('dragover');
    });
    dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragover'));
    dropZone.addEventListener('drop', e => {
        e.preventDefault();
        dropZone.classList.remove('dragover');
        if (e.dataTransfer.files.length > 0) {
            handleFile(e.dataTransfer.files[0]);
        }
    });

    fileInput.addEventListener('change', e => {
        if (e.target.files.length > 0) {
            handleFile(e.target.files[0]);
        }
    });

    removeBtn.addEventListener('click', () => {
        selectedFile = null;
        previewContainer.style.display = 'none';
        dropZone.style.display = 'block';
        analyzeBtn.disabled = true;
        fileInput.value = '';
    });

    function handleFile(file) {
        if (!file.type.match(/^image\/(png|jpeg|webp)$/)) {
            showError('PNG, JPG, WEBP 형식만 지원합니다.');
            return;
        }
        selectedFile = file;
        const reader = new FileReader();
        reader.onload = e => {
            previewImage.src = e.target.result;
            previewContainer.style.display = 'block';
            dropZone.style.display = 'none';
            analyzeBtn.disabled = false;
        };
        reader.readAsDataURL(file);
    }

    // 분석 시작
    analyzeBtn.addEventListener('click', async () => {
        if (!selectedFile) return;

        analyzeBtn.disabled = true;
        loading.style.display = 'block';
        resultSection.style.display = 'none';
        errorSection.style.display = 'none';

        const mappings = collectMappings();
        const formData = new FormData();
        formData.append('image', selectedFile);
        formData.append('mappings', JSON.stringify(mappings));

        try {
            const response = await fetch('/api/analyze', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                const errData = await response.json().catch(() => ({}));
                throw new Error(errData.message || `서버 오류 (${response.status})`);
            }

            const data = await response.json();
            displayResult(data);
        } catch (error) {
            showError(error.message);
        } finally {
            loading.style.display = 'none';
            analyzeBtn.disabled = false;
        }
    });

    function collectMappings() {
        const selects = document.querySelectorAll('#mapping-grid select');
        return Array.from(selects).map(s => ({
            colorName: s.dataset.color,
            type: s.value
        }));
    }

    function displayResult(data) {
        resultSection.style.display = 'block';
        document.getElementById('postit-count').textContent =
            `포스트잇: ${data.board.postIts.length}개`;
        document.getElementById('connection-count').textContent =
            `연결선: ${data.board.connections.length}개`;

        const tbody = document.querySelector('#postit-table tbody');
        tbody.innerHTML = '';
        data.board.postIts.forEach(p => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${escapeHtml(p.id)}</td>
                <td>${escapeHtml(p.text)}</td>
                <td>${escapeHtml(p.type)}</td>
                <td>${escapeHtml(p.detectedColor)}</td>
                <td>(${Math.round(p.position.x)}, ${Math.round(p.position.y)})</td>
            `;
            tbody.appendChild(tr);
        });

        document.getElementById('download-link').href = `/api/download/${data.sessionId}`;
        document.getElementById('xml-preview').textContent = data.drawioXml;

        document.getElementById('copy-xml-btn').onclick = () => {
            navigator.clipboard.writeText(data.drawioXml).then(() => {
                alert('XML이 클립보드에 복사되었습니다.');
            });
        };

        const markdown = generateMarkdown(data);

        document.getElementById('download-md-btn').onclick = () => {
            const blob = new Blob([markdown], {type: 'text/markdown;charset=utf-8'});
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'event-storming-result.md';
            a.click();
            URL.revokeObjectURL(url);
        };

        document.getElementById('copy-md-btn').onclick = () => {
            navigator.clipboard.writeText(markdown).then(() => {
                alert('Markdown이 클립보드에 복사되었습니다.');
            });
        };

        resultSection.scrollIntoView({behavior: 'smooth'});
    }

    function generateMarkdown(data) {
        const postIts = data.board.postIts;
        const connections = data.board.connections;
        let md = '# 이벤트 스토밍 분석 결과\n\n';
        md += `포스트잇: ${postIts.length}개 | 연결선: ${connections.length}개\n\n`;

        md += '## 포스트잇\n\n';
        md += '| ID | 텍스트 | 유형 | 색상 | 위치 |\n';
        md += '|----|--------|------|------|------|\n';
        postIts.forEach(p => {
            const pos = `(${Math.round(p.position.x)}, ${Math.round(p.position.y)})`;
            md += `| ${p.id} | ${p.text} | ${p.type} | ${p.detectedColor} | ${pos} |\n`;
        });

        if (connections.length > 0) {
            md += '\n## 연결선\n\n';
            md += '| ID | 출발 | 도착 | 라벨 |\n';
            md += '|----|------|------|------|\n';
            connections.forEach(c => {
                md += `| ${c.id} | ${c.fromId} | ${c.toId} | ${c.label || ''} |\n`;
            });
        }

        return md;
    }

    function showError(message) {
        errorSection.style.display = 'block';
        document.getElementById('error-message').textContent = message;
    }

    function escapeHtml(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }
});
