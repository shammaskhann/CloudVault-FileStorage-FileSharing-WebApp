import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import { fileAPI } from '../services/api';
import './Dashboard.css';

const Dashboard = () => {
  const { user, logout } = useAuth();
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [viewingFile, setViewingFile] = useState(null);
  const [shareMenuOpen, setShareMenuOpen] = useState(null);
  const fileInputRef = useRef(null);
  const shareMenuRef = useRef(null);

  useEffect(() => {
    fetchFiles();
  }, []);

  // Close share menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (shareMenuRef.current && !shareMenuRef.current.contains(event.target)) {
        setShareMenuOpen(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const fetchFiles = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await fileAPI.getMyFiles();
      
      if (response.status && response.files) {
        setFiles(response.files);
      } else {
        setError('Failed to fetch files');
      }
    } catch (err) {
      setError(err.message || 'Failed to fetch files');
    } finally {
      setLoading(false);
    }
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setUploading(true);
    setError('');
    setSuccess('');

    try {
      console.log(file);  
      const response = await fileAPI.uploadFile(file);
      
      if (response.status) {
        setSuccess('File uploaded successfully!');
        fetchFiles(); // Refresh file list
        if (fileInputRef.current) {
          fileInputRef.current.value = '';
        }
      } else {
        setError('Upload failed');
      }
    } catch (err) {
      setError(err.message || 'File upload failed');
    } finally {
      setUploading(false);
    }
  };

  const handleDownload = async (file) => {
    try {
      setError('');
      // Extract key from S3 URL
      // URL format: https://bucket.s3.region.amazonaws.com/key or https://s3.region.amazonaws.com/bucket/key
      let key = file.fileLink;
      try {
        const url = new URL(file.fileLink);
        // Get the pathname and remove leading slash
        key = url.pathname.substring(1);
        // If pathname is empty, try to get from the hostname pattern
        if (!key && url.hostname.includes('.s3.')) {
          const parts = url.hostname.split('.');
          // For bucket.s3.region format, the key is in the pathname
          key = url.pathname.substring(1);
        }
      } catch (e) {
        // If URL parsing fails, try to extract from string
        const urlParts = file.fileLink.split('/');
        key = urlParts[urlParts.length - 1];
      }
      
      if (!key) {
        throw new Error('Could not extract file key from URL');
      }
      
      const blob = await fileAPI.downloadFile(key);
      
      // Extract filename for download
      const filename = key.split('/').pop() || 'download';
      
      // Create download link
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      
      setSuccess('File downloaded successfully!');
    } catch (err) {
      setError(err.message || 'File download failed');
    }
  };

  const handleDelete = async (fileId) => {
    if (!window.confirm('Are you sure you want to delete this file?')) {
      return;
    }

    try {
      setError('');
      setSuccess('');
      
      const response = await fileAPI.deleteFile(fileId);
      
      if (response.status) {
        setSuccess('File deleted successfully!');
        fetchFiles(); // Refresh file list
      } else {
        setError('Delete failed');
      }
    } catch (err) {
      setError(err.message || 'File deletion failed');
    }
  };

  const handleView = async (file) => {
    try {
      setError('');
      setViewingFile(file);
    } catch (err) {
      setError(err.message || 'Failed to view file');
    }
  };

  const closeViewer = () => {
    setViewingFile(null);
  };

  const getFileType = (url) => {
    const extension = url.split('.').pop().toLowerCase();
    const imageTypes = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'];
    const videoTypes = ['mp4', 'webm', 'ogg'];
    const pdfTypes = ['pdf'];
    
    if (imageTypes.includes(extension)) return 'image';
    if (videoTypes.includes(extension)) return 'video';
    if (pdfTypes.includes(extension)) return 'pdf';
    return 'other';
  };

  const getFileName = (url) => {
    return url.split('/').pop();
  };

  const handleCopyLink = async (fileLink) => {
    try {
      await navigator.clipboard.writeText(fileLink);
      setSuccess('Link copied to clipboard!');
      setShareMenuOpen(null);
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Failed to copy link');
    }
  };

  const handleShareWhatsApp = (fileLink) => {
    const message = `Check out this file: ${getFileName(fileLink)}\n${fileLink}`;
    const whatsappUrl = `https://wa.me/?text=${encodeURIComponent(message)}`;
    window.open(whatsappUrl, '_blank');
    setShareMenuOpen(null);
  };

  const handleShareEmail = (fileLink) => {
    const subject = `File: ${getFileName(fileLink)}`;
    const body = `Check out this file:\n${fileLink}`;
    const mailtoUrl = `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
    window.location.href = mailtoUrl;
    setShareMenuOpen(null);
  };

  const toggleShareMenu = (fileId) => {
    setShareMenuOpen(shareMenuOpen === fileId ? null : fileId);
  };

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-content">
          <h1>CloudVault</h1>
          <div className="header-actions">
            <span className="user-name">
              {user?.username || user?.email || 'User'}
            </span>
            <button onClick={logout} className="logout-button">
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="dashboard-main">
        <div className="dashboard-content">
          <div className="upload-section">
            <h2>Upload Files</h2>
            <div className="upload-area">
              <input
                ref={fileInputRef}
                type="file"
                onChange={handleFileUpload}
                disabled={uploading}
                style={{ display: 'none' }}
                id="file-upload"
              />
              <label htmlFor="file-upload" className="upload-button">
                {uploading ? 'Uploading...' : 'Choose File to Upload'}
              </label>
            </div>
          </div>

          {error && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}

          <div className="files-section">
            <h2>My Files</h2>
            {loading ? (
              <div className="loading">Loading files...</div>
            ) : files.length === 0 ? (
              <div className="empty-state">
                <p>No files uploaded yet. Upload your first file to get started!</p>
              </div>
            ) : (
              <div className="files-grid">
                {files.map((file) => (
                  <div key={file.id} className="file-card">
                    <div className="file-preview" onClick={() => handleView(file)}>
                      {getFileType(file.fileLink) === 'image' ? (
                        <>
                          <img
                            src={file.fileLink}
                            alt={getFileName(file.fileLink)}
                            className="file-preview-image"
                            onError={(e) => {
                              e.target.style.display = 'none';
                              const iconDiv = e.target.parentElement.querySelector('.file-icon');
                              if (iconDiv) iconDiv.style.display = 'flex';
                            }}
                          />
                          <div className="file-icon" style={{ display: 'none' }}>
                            🖼️
                          </div>
                        </>
                      ) : (
                        <div className="file-icon">
                          {getFileType(file.fileLink) === 'video' ? '🎥' : 
                           getFileType(file.fileLink) === 'pdf' ? '📄' : '📁'}
                        </div>
                      )}
                    </div>
                    <div className="file-info">
                      <h3 className="file-name" title={getFileName(file.fileLink)}>
                        {getFileName(file.fileLink)}
                      </h3>
                    </div>
                    <div className="file-actions">
                      <button
                        onClick={() => handleView(file)}
                        className="action-button view-button"
                        title="View"
                      >
                        <span className="button-icon">👁️</span>
                        <span className="button-text">View</span>
                      </button>
                      <button
                        onClick={() => handleDownload(file)}
                        className="action-button download-button"
                        title="Download"
                      >
                        <span className="button-icon">⬇️</span>
                        <span className="button-text">Download</span>
                      </button>
                      <div className="share-menu-container" ref={shareMenuRef}>
                        <button
                          onClick={() => toggleShareMenu(file.id)}
                          className="action-button share-button"
                          title="Share"
                        >
                          <span className="button-icon">🔗</span>
                          <span className="button-text">Share</span>
                        </button>
                        {shareMenuOpen === file.id && (
                          <div className="share-menu">
                            <button
                              onClick={() => handleCopyLink(file.fileLink)}
                              className="share-menu-item"
                            >
                              <span className="menu-icon">📋</span>
                              <span>Copy Link</span>
                            </button>
                            <button
                              onClick={() => handleShareWhatsApp(file.fileLink)}
                              className="share-menu-item"
                            >
                              <span className="menu-icon">💬</span>
                              <span>WhatsApp</span>
                            </button>
                            <button
                              onClick={() => handleShareEmail(file.fileLink)}
                              className="share-menu-item"
                            >
                              <span className="menu-icon">📧</span>
                              <span>Email</span>
                            </button>
                          </div>
                        )}
                      </div>
                      <button
                        onClick={() => handleDelete(file.id)}
                        className="action-button delete-button"
                        title="Delete"
                      >
                        <span className="button-icon">🗑️</span>
                        <span className="button-text">Delete</span>
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>

      {viewingFile && (
        <div className="file-viewer-overlay" onClick={closeViewer}>
          <div className="file-viewer-content" onClick={(e) => e.stopPropagation()}>
            <button className="close-viewer" onClick={closeViewer}>
              ✕
            </button>
            <div className="viewer-body">
              {getFileType(viewingFile.fileLink) === 'image' ? (
                <img
                  src={viewingFile.fileLink}
                  alt={getFileName(viewingFile.fileLink)}
                  className="viewer-image"
                />
              ) : getFileType(viewingFile.fileLink) === 'video' ? (
                <video
                  src={viewingFile.fileLink}
                  controls
                  className="viewer-video"
                />
              ) : getFileType(viewingFile.fileLink) === 'pdf' ? (
                <iframe
                  src={viewingFile.fileLink}
                  title={getFileName(viewingFile.fileLink)}
                  className="viewer-iframe"
                />
              ) : (
                <div className="viewer-other">
                  <p>Preview not available for this file type.</p>
                  <a
                    href={viewingFile.fileLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="view-link"
                  >
                    Open in new tab
                  </a>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;

