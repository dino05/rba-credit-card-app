import React, { useState, useEffect } from 'react';
import ClientForm from './components/ClientForm';
import ClientList from './components/ClientList';
import SearchClient from './components/SearchClient';
import { clientAPI } from './services/api';
import './App.css';

function App() {
  const [clients, setClients] = useState([]);
  const [pagination, setPagination] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [sortBy, setSortBy] = useState('firstName');
  const [sortDirection, setSortDirection] = useState('asc');
  const [loading, setLoading] = useState(false);

  const fetchClients = async (page = currentPage) => {
    setLoading(true);
    try {
      const response = await clientAPI.getClients(page, pageSize, sortBy, sortDirection);
      setClients(response.data.content);
      setPagination({
        currentPage: response.data.currentPage,
        totalPages: response.data.totalPages,
        totalItems: response.data.totalItems
      });
    } catch (error) {
      console.error('Error fetching clients:', error);
      alert('Error loading clients');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClients();
  }, [currentPage, pageSize, sortBy, sortDirection]);

  const handleClientCreated = (newClient) => {
    fetchClients(0);
  };

  const handleClientDeleted = (deletedOib) => {
    setClients(clients.filter(client => client.oib !== deletedOib));
    fetchClients(currentPage);
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1>Credit Card Application Manager</h1>
      </header>

      <main className="app-main">
        <div className="sidebar">
          <ClientForm onClientCreated={handleClientCreated} />
          <SearchClient />
        </div>

        <div className="content">
          <div className="controls">
            <div className="control-group">
              <label>
                Page Size:
                <select 
                  value={pageSize} 
                  onChange={(e) => setPageSize(Number(e.target.value))}
                >
                  <option value="5">5</option>
                  <option value="10">10</option>
                  <option value="20">20</option>
                  <option value="50">50</option>
                </select>
              </label>
            </div>

            <div className="control-group">
              <label>
                Sort By:
                <select 
                  value={sortBy} 
                  onChange={(e) => setSortBy(e.target.value)}
                >
                  <option value="firstName">First Name</option>
                  <option value="lastName">Last Name</option>
                  <option value="createdAt">Created Date</option>
                </select>
              </label>
            </div>

            <div className="control-group">
              <label>
                Direction:
                <select 
                  value={sortDirection} 
                  onChange={(e) => setSortDirection(e.target.value)}
                >
                  <option value="asc">Ascending</option>
                  <option value="desc">Descending</option>
                </select>
              </label>
            </div>

            <button onClick={() => fetchClients(0)} disabled={loading}>
              {loading ? 'Refreshing...' : 'Refresh'}
            </button>
          </div>

          <ClientList
            clients={clients}
            pagination={pagination}
            onPageChange={handlePageChange}
            onClientDeleted={handleClientDeleted}
          />
        </div>
      </main>
    </div>
  );
}

export default App;