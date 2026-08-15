# 1. Create a bin directory in your home folder if it doesn't exist
mkdir -p ~/bin

# 2. Download the latest mkcert binary (v1.4.4)
curl -JLO "https://dl.filippo.io/mkcert/latest?for=linux/amd64"

# 3. Rename it to 'mkcert' and make it executable
mv mkcert-v* ~/bin/mkcert
chmod +x ~/bin/mkcert

# 4. Add ~/bin to your PATH (if it isn't already)
export PATH="$HOME/bin:$PATH"

# 5. Add to ~/.bashrc or ~/.zshrc so it stays permanent
echo 'export PATH="$HOME/bin:$PATH"' >> ~/.bashrc