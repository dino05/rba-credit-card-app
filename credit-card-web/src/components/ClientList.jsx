import React from 'react';
import { clientAPI } from '../services/api';

const ClientList = ({ clients, pagination, onPageChange, onClientDeleted }) => {
  const handleDelete = async (oib) => {
    if (window.confirm(`Are you sure you want to delete client with OIB: ${oib}?`)) {
      try {
        await clientAPI.deleteClient(oib);
        if (onClientDeleted) {
          onClientDeleted(oib);
        }
        alert('Client deleted successfully!');
      } catch (error) {
        alert('Error deleting client: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  if (!clients || clients.length === 0) {
    return <div className="card">No clients found.</div>;
  }

  return (
    <div className="card">
      <h2>Clients List</h2>
      
      {pagination && (
        <div className="pagination-info">
          Page {pagination.currentPage + 1} of {pagination.totalPages} 
          | Total clients: {pagination.totalItems}
        </div>
      )}

      <div className="clients-grid">
        {clients.map(client => (
          <div key={client.id} className="client-card">
            <div className="client-info">
              <strong>{client.firstName} {client.lastName}</strong>
              <div>OIB: {client.oib}</div>
              <div>Status: <span className={`status-${client.cardStatus.toLowerCase()}`}>
                {client.cardStatus}
              </span></div>
              <div>Created: {new Date(client.createdAt).toLocaleDateString()}</div>
            </div>
            <button 
              onClick={() => handleDelete(client.oib)}
              className="btn-danger"
            >
              Delete
            </button>
          </div>
        ))}
      </div>

      {pagination && pagination.totalPages > 1 && (
        <div className="pagination-controls">
          <button 
            onClick={() => onPageChange(pagination.currentPage - 1)}
            disabled={pagination.currentPage === 0}
          >
            Previous
          </button>
          
          <span>Page {pagination.currentPage + 1} of {pagination.totalPages}</span>
          
          <button 
            onClick={() => onPageChange(pagination.currentPage + 1)}
            disabled={pagination.currentPage === pagination.totalPages - 1}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default ClientList;