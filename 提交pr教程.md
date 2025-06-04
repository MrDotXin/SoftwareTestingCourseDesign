以下是一份详细的 GitHub 提交 Pull Request（PR）的教程，适用于新手用户参与开源项目贡献：

---

### **GitHub 提交 Pull Request（PR）完整教程**
#### **步骤 1：Fork 目标仓库**
1. 访问你想贡献的项目仓库（如 `https://github.com/owner/repo`）。
2. 点击右上角的 **Fork** 按钮，将仓库复制到你的 GitHub 账号下。

#### **步骤 2：克隆你的 Fork 到本地**
```bash
git clone https://github.com/你的用户名/repo.git
cd repo
```

#### **步骤 3：设置上游远程仓库（可选但推荐）**

```bash
git remote add upstream https://github.com/owner/repo.git
```
- 后续可通过 `git fetch upstream` 同步原仓库的更新。

#### **步骤 4：创建新分支**
```bash
git checkout -b your-branch-name
```
- 分支名建议描述修改内容（如 `fix-typo` 或 `add-feature-x`）。

#### **步骤 5：修改代码并提交**
1. 在本地完成代码或文档修改。
2. 提交更改：
   ```bash
   git add .
   git commit -m "描述你的修改（简明扼要）"
   ```

#### **步骤 6：推送分支到你的 Fork**
```bash
git push origin your-branch-name
```

#### **步骤 7：创建 Pull Request**
1. 访问你的 Fork 仓库页面（`https://github.com/你的用户名/repo`）。
2. GitHub 通常会提示 `Compare & pull request`，点击它。
   - 如果没有提示，切换到你的分支后点击 **Contribute > Open Pull Request**。
3. 填写 PR 信息：
   - **标题**：概括修改内容（如 "Fix typo in README"）。
   - **描述**：详细说明修改原因、关联的 Issue（如 `Closes #123`）。
4. 点击 **Create pull request**。

#### **步骤 8：与维护者互动**
- 根据反馈调整代码（后续提交会自动更新 PR）。
- 如果原仓库有更新，可通过以下操作同步：
  ```bash
  git fetch upstream
  git rebase upstream/main  # 或分支名
  git push -f origin your-branch-name
  ```

#### **注意事项**
1. **代码风格**：遵循项目的规范（如缩进、注释等）。
2. **测试**：确保修改通过项目的测试（如果有）。
3. **小步提交**：一个 PR 尽量只解决一个问题。
4. **检查 CI**：等待自动化测试（如 GitHub Actions）通过。

#### **常见问题**

- **权限不足**：确保你 Fork 的是最新版本，且分支正确。
- **冲突解决**：使用 `git rebase` 或网页端的冲突解决工具。

