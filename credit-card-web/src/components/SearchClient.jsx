import React, { useState } from 'react';
import { clientAPI } from '../services/api';

const SearchClient = ({ onClientFound }) => {
  const [searchOib, setSearchOib] = useState('');
  const [searchResult, setSearchResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    
    if (searchOib.length !== 11) {
      setMessage('OIB must be exactly 11 characters');
      return;
    }

    setLoading(true);
    setMessage('');
    setSearchResult(null);

    try {
      console.log('Searching for OIB:', searchOib);
      const response = await clientAPI.getClientByOib(searchOib);
      console.log('Search response:', response);
      
      setSearchResult(response.data);
      setMessage('Client found!');
      
      if (onClientFound) {
        onClientFound(response.data);
      }
    } catch (error) {
      console.error('Search error details:', error);
      
      // Improved error handling
      if (error.isNetworkError) {
        setMessage('Network error: Cannot connect to server. Make sure the backend is running.');
      } else if (error.isTimeout) {
        setMessage('Request timeout: Server is taking too long to respond.');
      } else if (error.status === 404) {
        setMessage(`Client with OIB ${searchOib} not found`);
      } else if (error.status === 500) {
        setMessage('Server error: Please try again later.');
      } else if (error.status >= 400 && error.status < 500) {
        setMessage(`Client error: ${error.data?.message || 'Invalid request'}`);
      } else {
        setMessage(`Error searching for client: ${error.message}`);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>Search Client by OIB</h2>
      
      <form onSubmit={handleSearch} className="search-form">
        <div className="form-group">
          <input
            type="text"
            placeholder="Enter OIB (11 characters)"
            value={searchOib}
            onChange={(e) => setSearchOib(e.target.value)}
            maxLength="11"
            required
          />
          <button type="submit" disabled={loading || searchOib.length !== 11}>
            {loading ? 'Searching...' : 'Search'}
          </button>
        </div>
      </form>

      {message && (
        <div className={`message ${
          message.includes('error') || message.includes('Error') || message.includes('not found') 
            ? 'error' 
            : 'success'
        }`}>
          {message}
        </div>
      )}

      {searchResult && (
        <div className="search-result">
          <h3>Client Found:</h3>
          <div className="client-details">
            <p><strong>Name:</strong> {searchResult.firstName} {searchResult.lastName}</p>
            <p><strong>OIB:</strong> {searchResult.oib}</p>
            <p><strong>Status:</strong> {searchResult.cardStatus}</p>
            <p><strong>Created:</strong> {new Date(searchResult.createdAt).toLocaleString()}</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchClient;