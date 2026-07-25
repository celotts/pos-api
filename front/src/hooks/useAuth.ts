import { useSelector } from 'react-redux'
import { useMemo } from 'react'
import { selectCurrentUser } from '../store/slices/authSlice'

export const useAuth = () => {
  const user = useSelector(selectCurrentUser)

  // Usamos useMemo para evitar recalcular en cada render, a menos que el usuario cambie.
  const roles = useMemo(() => user?.roles || [], [user])

  const isAdmin = useMemo(() => roles.includes('ADMIN'), [roles])
  const isEditor = useMemo(() => roles.includes('EDITOR'), [roles])
  const isUser = useMemo(() => roles.includes('USER'), [roles])

  return {
    user,
    isAdmin,
    isEditor,
    isUser,
  }
}
