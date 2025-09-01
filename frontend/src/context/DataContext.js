import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { machinesAPI, facilitiesAPI, usageAPI } from '../services/api';

const DataContext = createContext();

export const useData = () => {
    const context = useContext(DataContext);
    if (!context) {
        throw new Error('useData must be used within a DataProvider');
    }
    return context;
};

export const DataProvider = ({ children }) => {
    const [machines, setMachines] = useState([]);
    const [facilities, setFacilities] = useState([]);
    const [usageHistory, setUsageHistory] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [lastUpdate, setLastUpdate] = useState(null);

    // Centralized data fetching
    const fetchAllData = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            
            const [machinesData, facilitiesData, usageData] = await Promise.allSettled([
                machinesAPI.getAll(),
                facilitiesAPI.getAll(),
                usageAPI.getAll()
            ]);

            // Handle each result separately to prevent one failure from breaking everything
            setMachines(machinesData.status === 'fulfilled' ? (machinesData.value || []) : []);
            setFacilities(facilitiesData.status === 'fulfilled' ? (facilitiesData.value || []) : []);
            setUsageHistory(usageData.status === 'fulfilled' ? (usageData.value || []) : []);
            setLastUpdate(new Date());
            
            // Set error only if all requests failed
            if (machinesData.status === 'rejected' && facilitiesData.status === 'rejected' && usageData.status === 'rejected') {
                setError('Failed to fetch data from all sources');
            }
            
        } catch (err) {
            setError('Failed to fetch data');
            console.error('Data fetch error:', err);
        } finally {
            setLoading(false);
        }
    }, []);

    // Update machine status
    const updateMachineStatus = useCallback(async (machineId, status) => {
        try {
            await machinesAPI.updateStatus(machineId, status);
            // Refresh all data to ensure consistency
            await fetchAllData();
            return true;
        } catch (err) {
            setError('Failed to update machine status');
            console.error('Status update error:', err);
            return false;
        }
    }, [fetchAllData]);

    // Get machine by ID
    const getMachineById = useCallback((machineId) => {
        return machines.find(m => m.id === parseInt(machineId));
    }, [machines]);

    // Get facility by ID
    const getFacilityById = useCallback((facilityId) => {
        return facilities.find(f => f.id === parseInt(facilityId));
    }, [facilities]);

    // Get machines by facility
    const getMachinesByFacility = useCallback((facilityId) => {
        return machines.filter(m => m.facilityId === parseInt(facilityId));
    }, [machines]);

    // Get machines by status
    const getMachinesByStatus = useCallback((status) => {
        return machines.filter(m => m.status === status);
    }, [machines]);

    // Get usage count for a machine
    const getUsageCountForMachine = useCallback((machineId) => {
        return usageHistory.filter(u => u.machineId === parseInt(machineId)).length;
    }, [usageHistory]);

    // Get total usage count
    const getTotalUsageCount = useCallback(() => {
        return usageHistory.length;
    }, [usageHistory]);

    // Get today's usage count
    const getTodayUsageCount = useCallback(() => {
        const today = new Date().toDateString();
        return usageHistory.filter(u => 
            new Date(u.timestamp).toDateString() === today
        ).length;
    }, [usageHistory]);

    // Get active machines count
    const getActiveMachinesCount = useCallback(() => {
        return machines.filter(m => m.status === 'ON' && m.isActive).length;
    }, [machines]);

    // Get low supply machines
    const getLowSupplyMachines = useCallback(() => {
        return machines.filter(m => m.hasLowSupplies);
    }, [machines]);

    // Dashboard statistics
    const getDashboardStats = useCallback(() => {
        return {
            totalMachines: machines.length,
            activeMachines: getActiveMachinesCount(),
            totalFacilities: facilities.length,
            totalUsage: getTotalUsageCount(),
            todayUsage: getTodayUsageCount(),
            lowSupplyMachines: getLowSupplyMachines().length,
            lastUpdate: lastUpdate
        };
    }, [machines, facilities, getActiveMachinesCount, getTotalUsageCount, getTodayUsageCount, getLowSupplyMachines, lastUpdate]);

    // Auto-refresh data every 10 seconds
    useEffect(() => {
        fetchAllData();
        
        const interval = setInterval(() => {
            fetchAllData();
        }, 10000); // 10 seconds

        return () => clearInterval(interval);
    }, [fetchAllData]);

    const value = {
        // Data
        machines,
        facilities,
        usageHistory,
        loading,
        error,
        lastUpdate,
        
        // Actions
        fetchAllData,
        updateMachineStatus,
        
        // Getters
        getMachineById,
        getFacilityById,
        getMachinesByFacility,
        getMachinesByStatus,
        getUsageCountForMachine,
        getTotalUsageCount,
        getTodayUsageCount,
        getActiveMachinesCount,
        getLowSupplyMachines,
        getDashboardStats,
        
        // Setters
        setMachines,
        setFacilities,
        setUsageHistory,
        setError
    };

    return (
        <DataContext.Provider value={value}>
            {children}
        </DataContext.Provider>
    );
};