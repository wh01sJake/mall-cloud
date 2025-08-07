<script setup>
    import {
        Management,
        Promotion,
        UserFilled,
        User,
        Crop,
        EditPen,
        SwitchButton,
        CaretBottom,
        Document,
        DataAnalysis,
        TrendCharts,
        Menu as MenuIcon
    } from '@element-plus/icons-vue'
    import avatar from '@/assets/default.png'

    // Function called when menu item is clicked
    import {useRouter} from 'vue-router'

    const router = useRouter();
    import {ElMessage, ElMessageBox} from 'element-plus'

    import {useAdminInfoStore} from '@/store/adminInfo.js'
    import adminApi from "@/api/admin.js";
    import {useTokenStore} from "@/store/token.js";
    import {ref, computed} from "vue";

    const adminInfoStore = useAdminInfoStore()
    const tokenStore = useTokenStore()

    // Mobile menu state
    const isMobileMenuOpen = ref(false)
    const isMobile = ref(false)

    // Check if mobile
    const checkMobile = () => {
        isMobile.value = window.innerWidth <= 768
        if (!isMobile.value) {
            isMobileMenuOpen.value = false
        }
    }

    // Toggle mobile menu
    const toggleMobileMenu = () => {
        isMobileMenuOpen.value = !isMobileMenuOpen.value
    }

    // Initialize mobile check and add resize listener
    import { onMounted, onUnmounted } from 'vue'

    onMounted(() => {
        checkMobile()
        window.addEventListener('resize', checkMobile)
    })

    onUnmounted(() => {
        window.removeEventListener('resize', checkMobile)
    })


    const getAdminInfo = () => {
        adminApi.adminInfo().then(result =>{
            if (result.code == 0){
                adminInfoStore.setAdminInfo(result.data);
            }
        })
    }

    getAdminInfo()

    const dialogUpdateAdminInfoVisible = ref(false)
    const adminUser = ref({})



    const handleCommand = (command) => {
        // Check command
        if (command === 'logout') {
            // Logout
            tokenStore.removeToken()
            adminInfoStore.removeAdminInfo()
            router.push('/login')
        }
        else if (command === 'updateAdminInfo'){
            dialogUpdateAdminInfoVisible.value = true

            // Clear and properly initialize adminUser
            adminUser.value = {
                id: adminInfoStore.admin.id,
                username: adminInfoStore.admin.username,
                email: adminInfoStore.admin.email,
                phone: adminInfoStore.admin.phone,
                avatar: adminInfoStore.admin.avatar || ''
            }

            console.log('Initialized adminUser:', adminUser.value)
        }
        else if(command === 'resetPassword'){
            dialogResetPasswordVisible.value = true
        }
        else {
            // Route navigation
            router.push('/admin/' + command)
        }
    }

    const dialogResetPasswordVisible = ref(false)

    const adminPasswordDTO = ref({
        oldPassword : '',
        newPassword : ''
    })

    const resetForm = ref()
    // Custom password confirmation validation function
    const rePasswordValid = (rule, value, callback) => {
        if (value == null || value === '') {
            return callback(new Error('Please confirm password again'))
        }
        // For reactive objects: use registerData.value to get the value
        if (adminPasswordDTO.value.newPassword !== value) {
            return callback(new Error('Passwords do not match'))
        }

        callback()
    }
    const rules = ref({
        oldPassword: [
            {required: true, message: 'Please enter password', trigger: 'blur'},
            {min: 3, max: 16, message: 'Password length must be 3-16 characters', trigger: 'blur'}
        ],
        newPassword: [
            {required: true, message: 'Please enter password', trigger: 'blur'},
            {min: 3, max: 16, message: 'Password length must be 3-16 characters', trigger: 'blur'}
        ],
        reNewPassword: [
            {required: true, message: 'Please enter password', trigger: 'blur'},
            {validator: rePasswordValid, trigger: 'blur' }
        ]
    })
    const resetPassword = async (formEl) => {
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                adminApi.resetPassword(adminPasswordDTO.value).then(result => {
                    if (result.code === 0) {
                        ElMessage.success(result.msg)
                        dialogResetPasswordVisible.value = false
                        tokenStore.removeToken();
                        adminInfoStore.removeAdminInfo();
                        // Redirect to login
                        router.push('/login')
                    } else {
                        ElMessage.error(result.msg)
                    }
                })
            } else {
                ElMessage.error('Form validation failed');
            }
        })
    }

    const updateAdminInfo = () =>{
        // Only send the fields that should be updated
        const updateData = {
            id: adminUser.value.id,
            username: adminUser.value.username,
            email: adminUser.value.email,
            phone: adminUser.value.phone,
            avatar: adminUser.value.avatar
        }

        console.log('Sending update data:', updateData)

        adminApi.updateById(updateData).then(result => {
            if (result.code == 0){
                ElMessage.success(result.msg )
                dialogUpdateAdminInfoVisible.value = false
                getAdminInfo()
            }
            else {
                ElMessage.error(result.msg)
            }
        })

    }

    const handleAvatarSuccess = (result) => {
        console.log('Upload result:', result)
        console.log('Upload result type:', typeof result)
        console.log('Upload result.data:', result.data)
        console.log('Upload result.data.url:', result.data?.url)

        if (result.code === 0 && result.data && result.data.url) {
            adminUser.value.avatar = result.data.url
            console.log('Set avatar to:', adminUser.value.avatar)
            console.log('Avatar type:', typeof adminUser.value.avatar)
        } else {
            ElMessage.error('Upload failed: ' + (result.msg || 'Unknown error'))
        }
    }


    const headers = ref({
        Authorization: `Bearer ${tokenStore.token}`
    })

    // Computed property to ensure avatar is always a string
    const avatarUrl = computed(() => {
        const avatar = adminUser.value.avatar
        console.log('Computing avatarUrl, avatar value:', avatar, 'type:', typeof avatar)

        if (typeof avatar === 'string') {
            return avatar
        } else if (avatar && typeof avatar === 'object' && avatar.url) {
            return avatar.url
        } else {
            return ''
        }
    })

    // Function to get the correct upload URL
    const getUploadUrl = (path) => {
        // In development, use the proxy configuration
        if (import.meta.env.DEV) {
            return `/api${path}`
        }
        // In production, use the full gateway URL
        return `${import.meta.env.VITE_API_BASE_URL || 'https://vapemall-gateway-d8a215a380c5.herokuapp.com/api'}${path}`
    }

    // Page title and subtitle based on current route
    const getPageTitle = () => {
        const route = router.currentRoute.value
        const titleMap = {
            '/admin': 'Dashboard',
            '/customer': 'Customer Management',
            '/product': 'Product Management',
            '/category': 'Category Management',
            '/order': 'Order Management',
            '/order-chart': 'Order Analytics',
            '/chart': 'Category Analytics'
        }
        return titleMap[route.path] || 'Admin Dashboard'
    }

    const getPageSubtitle = () => {
        const route = router.currentRoute.value
        const subtitleMap = {
            '/admin': 'Overview of your business metrics and performance',
            '/customer': 'Manage customer accounts and information',
            '/product': 'Add, edit, and organize your product catalog',
            '/category': 'Organize products into categories',
            '/order': 'Track and manage customer orders',
            '/order-chart': 'Analyze order trends and performance',
            '/chart': 'View category performance metrics'
        }
        return subtitleMap[route.path] || 'Manage your e-commerce platform'
    }
</script>

<template>
	<!-- Element Plus container -->
	<el-container class="layout-container">
		<!-- Left sidebar menu -->
		<el-aside
			:width="isMobile ? (isMobileMenuOpen ? '260px' : '0px') : '260px'"
			class="admin-sidebar"
			:class="{ 'mobile-sidebar': isMobile, 'mobile-open': isMobileMenuOpen }"
		>
			<div class="sidebar-header">
				<div class="logo">
					<div class="logo-icon">🛒</div>
					<h2 class="logo-text">VapeMall</h2>
				</div>
				<p class="logo-subtitle">Admin Dashboard</p>
			</div>

			<!-- Element Plus menu component -->
			<el-menu
				class="admin-menu"
				active-text-color="#ffffff"
				background-color="transparent"
				text-color="#a0a0a0"
				router
				:default-active="$route.path"
			>
				<el-menu-item index="/admin" class="menu-item">
					<el-icon class="menu-icon">
						<Management/>
					</el-icon>
					<span class="menu-text">Dashboard</span>
				</el-menu-item>

				<el-menu-item index="/customer" class="menu-item">
					<el-icon class="menu-icon">
						<User/>
					</el-icon>
					<span class="menu-text">Customers</span>
				</el-menu-item>

				<el-menu-item index="/product" class="menu-item">
					<el-icon class="menu-icon">
						<Promotion/>
					</el-icon>
					<span class="menu-text">Products</span>
				</el-menu-item>

                <el-menu-item index="/category" class="menu-item">
                    <el-icon class="menu-icon">
                        <Promotion/>
                    </el-icon>
                    <span class="menu-text">Categories</span>
                </el-menu-item>

				<el-menu-item index="/order" class="menu-item">
					<el-icon class="menu-icon">
						<Document/>
					</el-icon>
					<span class="menu-text">Orders</span>
				</el-menu-item>

                <el-menu-item index="/order-chart" class="menu-item">
                    <el-icon class="menu-icon">
                        <TrendCharts/>
                    </el-icon>
                    <span class="menu-text">Order Analytics</span>
                </el-menu-item>

                <el-menu-item index="/chart" class="menu-item">
                    <el-icon class="menu-icon">
                        <DataAnalysis/>
                    </el-icon>
                    <span class="menu-text">Category Analytics</span>
                </el-menu-item>
			</el-menu>
		</el-aside>

		<!-- Right main area -->
		<el-container class="main-container">
			<!-- Header area -->
			<el-header class="admin-header">
				<div class="header-left">
					<!-- Mobile menu toggle button -->
					<el-button
						v-if="isMobile"
						@click="toggleMobileMenu"
						class="mobile-menu-toggle"
						:icon="MenuIcon"
						circle
						size="large"
					/>
					<div class="header-titles">
						<h1 class="page-title">{{ getPageTitle() }}</h1>
						<p class="page-subtitle">{{ getPageSubtitle() }}</p>
					</div>
				</div>

				<div class="header-right">
					<!-- User dropdown -->
					<el-dropdown placement="bottom-end" @command="handleCommand" class="user-dropdown">
	                    <div class="user-info">
	                        <el-avatar
	                        	:src="adminInfoStore.admin?.avatar || avatar"
	                        	class="user-avatar"
	                        	:size="36"
	                        />
	                        <div class="user-details">
	                        	<span class="user-name">{{ adminInfoStore.admin?.username || 'Admin' }}</span>
	                        	<span class="user-role">Administrator</span>
	                        </div>
	                        <el-icon class="dropdown-icon">
	                            <CaretBottom/>
	                        </el-icon>
	                    </div>
						<template #dropdown>
							<el-dropdown-menu class="user-dropdown-menu">
								<el-dropdown-item command="updateAdminInfo" :icon="User" class="dropdown-item">
									Profile Settings
								</el-dropdown-item>
								<el-dropdown-item command="resetPassword" :icon="EditPen" class="dropdown-item">
									Change Password
								</el-dropdown-item>
								<el-dropdown-item command="logout" :icon="SwitchButton" class="dropdown-item logout-item">
									Sign Out
								</el-dropdown-item>
							</el-dropdown-menu>
						</template>
					</el-dropdown>
				</div>
			</el-header>

			<!-- Main content area -->
			<el-main class="admin-main">
				<div class="content-wrapper">
					<router-view></router-view>
				</div>
			</el-main>

			<!-- Footer area -->
			<el-footer class="admin-footer">
				<div class="footer-content">
					<span>© 2025 VapeMall Admin Dashboard. All rights reserved.</span>
					<span>Created by Jake</span>
				</div>
			</el-footer>
		</el-container>
	</el-container>

	<!-- Mobile overlay -->
	<div
		v-if="isMobile && isMobileMenuOpen"
		class="mobile-overlay"
		@click="toggleMobileMenu"
	></div>

    <el-dialog v-model="dialogUpdateAdminInfoVisible" title="Update Profile" width="500" :lock-scroll="false">
    <el-form :model="adminUser">
        <el-form-item label="Name" :label-width="60">
            <el-input v-model="adminUser.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Email" :label-width="60">
            <el-input v-model="adminUser.email" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Phone" :label-width="60">
            <el-input v-model="adminUser.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Avatar" :label-width="60">
            <el-upload
                class="avatar-uploader"
                :action="getUploadUrl('/upload/user')"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :headers="headers">
                <img v-if="avatarUrl" :src="avatarUrl" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
        </el-form-item>
    </el-form>
    <template #footer>
        <div class="dialog-footer">
            <el-button @click="dialogUpdateAdminInfoVisible = false">Cancel</el-button>
            <el-button type="primary" @click="updateAdminInfo">
                Confirm
            </el-button>
        </div>
    </template>
    </el-dialog>
    <el-dialog  v-model="dialogResetPasswordVisible" title="Reset Password" width="500" :lock-scroll="false">
    <el-form ref="resetForm" :rules="rules" :model="adminPasswordDTO">
        <el-form-item prop="oldPassword" label="Old Password" :label-width="100">
            <el-input v-model="adminPasswordDTO.oldPassword" autocomplete="off"/>
        </el-form-item>
        <el-form-item prop="newPassword" label="New Password" :label-width="100">
            <el-input v-model="adminPasswordDTO.newPassword" autocomplete="off"/>
        </el-form-item>
        <el-form-item prop="reNewPassword" label="Confirm Password" :label-width="100">
            <el-input v-model="adminPasswordDTO.reNewPassword" autocomplete="off"/>
        </el-form-item>
    </el-form>
    <template #footer>
        <div class="dialog-footer">
            <el-button @click="dialogResetPasswordVisible = false">Cancel</el-button>
            <el-button type="primary" @click="resetPassword(resetForm)">
                Confirm
            </el-button>
        </div>
    </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
.layout-container {
    height: 100vh;
    background: #f8f9fa;

    .admin-sidebar {
        background: linear-gradient(180deg, #1a1a1a 0%, #2d2d2d 100%);
        border-right: 1px solid #333;
        box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);

        .sidebar-header {
            padding: 24px 20px;
            border-bottom: 1px solid #333;
            text-align: center;

            .logo {
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 12px;
                margin-bottom: 8px;

                .logo-icon {
                    font-size: 24px;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                    background-clip: text;
                }

                .logo-text {
                    font-size: 20px;
                    font-weight: 700;
                    color: #ffffff;
                    margin: 0;
                }
            }

            .logo-subtitle {
                color: #a0a0a0;
                font-size: 12px;
                font-weight: 500;
                margin: 0;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
        }

        .admin-menu {
            border: none;
            background: transparent;
            padding: 16px 12px;

            .menu-item {
                height: 44px;
                line-height: 44px;
                margin: 4px 0;
                border-radius: 8px;
                transition: all 0.3s ease;

                .menu-icon {
                    margin-right: 12px;
                    font-size: 16px;
                }

                .menu-text {
                    font-size: 14px;
                    font-weight: 500;
                }

                &:hover {
                    background: rgba(255, 255, 255, 0.1);
                    color: #ffffff;
                }

                &.is-active {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: #ffffff;
                    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
                }
            }
        }
    }

    .main-container {
        background: #ffffff;

        .admin-header {
            background: #ffffff;
            border-bottom: 1px solid #e5e7eb;
            padding: 0 32px;
            height: 80px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

            .header-left {
                .page-title {
                    font-size: 24px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0 0 4px 0;
                }

                .page-subtitle {
                    font-size: 14px;
                    color: #6b7280;
                    margin: 0;
                }
            }

            .header-right {
                .user-dropdown {
                    .user-info {
                        display: flex;
                        align-items: center;
                        gap: 12px;
                        padding: 8px 16px;
                        border-radius: 12px;
                        transition: all 0.3s ease;
                        cursor: pointer;

                        &:hover {
                            background: #f3f4f6;
                        }

                        .user-avatar {
                            border: 2px solid #e5e7eb;
                        }

                        .user-details {
                            display: flex;
                            flex-direction: column;
                            align-items: flex-start;

                            .user-name {
                                font-size: 14px;
                                font-weight: 600;
                                color: #1f2937;
                                line-height: 1.2;
                            }

                            .user-role {
                                font-size: 12px;
                                color: #6b7280;
                                line-height: 1.2;
                            }
                        }

                        .dropdown-icon {
                            color: #9ca3af;
                            font-size: 14px;
                        }
                    }
                }
            }
        }

        .admin-main {
            background: #f8f9fa;
            padding: 32px;
            min-height: calc(100vh - 140px);

            .content-wrapper {
                max-width: 100%;
                margin: 0 auto;
            }
        }

        .admin-footer {
            background: #ffffff;
            border-top: 1px solid #e5e7eb;
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: center;

            .footer-content {
                display: flex;
                align-items: center;
                justify-content: space-between;
                width: 100%;
                max-width: 1200px;
                padding: 0 32px;
                font-size: 13px;
                color: #6b7280;
            }
        }
    }
}
</style>
