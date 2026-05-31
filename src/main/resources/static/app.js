const API = '/api/enrollments';

function setMessage(text, isError) {
    const el = document.getElementById('message');
    el.textContent = text || '';
    el.className = 'row' + (isError ? ' error' : '');
}

// 将记录列表按课程类型分组（搜索结果是扁平列表时使用）
function groupByType(records) {
    const map = {};
    for (const r of records) {
        const type = r.courseType || '未分类';
        (map[type] = map[type] || []).push(r);
    }
    return map;
}

// 渲染：grouped 为 { 课程类型: [记录...] }
function renderGrouped(grouped) {
    const container = document.getElementById('result');
    container.innerHTML = '';
    const types = Object.keys(grouped);
    if (types.length === 0) {
        container.innerHTML = '<p class="hint">暂无数据</p>';
        return;
    }
    for (const type of types) {
        const title = document.createElement('div');
        title.className = 'group-title';
        title.textContent = `课程类型：${type}（${grouped[type].length} 条）`;
        container.appendChild(title);

        const table = document.createElement('table');
        table.innerHTML = '<tr><th>学生ID</th><th>课程ID</th><th>课程名称</th><th>课程类型</th></tr>';
        for (const r of grouped[type]) {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${r.studentId}</td><td>${r.courseId}</td>` +
                `<td>${r.courseName}</td><td>${r.courseType || ''}</td>`;
            table.appendChild(tr);
        }
        container.appendChild(table);
    }
}

async function doImport() {
    const csv = document.getElementById('csv').value;
    setMessage('处理中...');
    try {
        const t0 = performance.now();
        const resp = await fetch(`${API}/import`, {
            method: 'POST',
            headers: { 'Content-Type': 'text/plain' },
            body: csv
        });
        const data = await resp.json();
        renderGrouped(data.grouped);
        const cost = (performance.now() - t0).toFixed(0);
        setMessage(`导入完成，去重排序后共 ${data.total} 条，耗时 ${cost} ms`);
    } catch (e) {
        setMessage('导入失败：' + e.message, true);
    }
}

async function search(keyword) {
    const resp = await fetch(`${API}/search?keyword=${encodeURIComponent(keyword)}`);
    if (resp.status === 404) {
        const err = await resp.json();
        renderGrouped({});
        setMessage(err.message, true);
        return;
    }
    const list = await resp.json();
    renderGrouped(groupByType(list));
    setMessage(`检索到 ${list.length} 条记录`);
}

function doSearch() {
    search(document.getElementById('keyword').value);
}

function showAll() {
    document.getElementById('keyword').value = '';
    search('');
}

// 在搜索框按回车即触发检索，优化交互体验
document.getElementById('keyword')
    .addEventListener('keydown', e => { if (e.key === 'Enter') doSearch(); });
